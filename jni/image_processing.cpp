#include "image_processing.h"

/*!\fn get_cvmat_from_raw_data(CvMat *image, int rows, int cols, int format,
                            const unsigned char *rawData)
*\param [out] image - output image. 
*\param [in] rows - number of rows in the output matrix.
*\param [in] cols - number of columns in the output matrix.
*\param [in] format - if 0 then 24-bit image, if 1 - 32-bit image.
*\param [in] rawData - pointer to the image data.
*\return 1 if it couldn't to get image from raw data, 0 otherwise.
*/
int get_cvmat_from_raw_data(CvMat *image, int rows, int cols, int format,
                            const unsigned char *rawData)
{
    bool unknownFormat = false;
    switch (format)
    {
        case 0:
            cvInitMatHeader(image, rows, cols, CV_8UC3, (void*)rawData, cols*3);
            break;

        case 1:
            cvInitMatHeader(image, rows, cols, CV_8UC4, (void*)rawData, cols*4);
            break;

        default:
            unknownFormat = true;
            break;
    }

    return unknownFormat ? 1 : 0;
}


/*!\fn get_feature_vector_from_image(const CvMat *symbolImage, float *data)
 * \param [in] symbolImage - image to get features from.
 * \param [out] data - output array to write in
 *                                  (should have 25 members allocated).
 */
void get_feature_vector_from_image(const CvMat *symbolImage, float *data)
{
    // resize image
    CvMat *resizedImage = cvCreateMat(5, 5, symbolImage->type);
    cvResize(symbolImage, resizedImage, CV_INTER_CUBIC);

    // convert to image to float CV_32FC1 format
    CvMat *dataImage = cvCreateMat(5, 5, CV_32FC1);
    cvConvert(resizedImage, dataImage);

    // release resized image
    cvReleaseMat(&resizedImage);

    // copy data from converted image to array, normalizing to [-1;1] segment. 
    for (int i = 0; i < dataImage->rows; i++)
        for (int j = 0; j < dataImage->cols; j++)
        {
            float value = CV_MAT_ELEM(*dataImage, float, i, j);
            data[i*5 + j] = value*(float)2/255 - 1;
        }

    // release converted image
    cvReleaseMat(&dataImage);

}


/*!\fn get_symbol_templates(const char *symbolName)
 * \param [in] symbolName - specified symbol name.
 * \return corresponding array of templates.
 */
float **get_symbol_templates(const char *symbolName)
{
    float **templates = (float **)malloc(8 * sizeof(float*));

    if (strcmp(symbolName, "V") == 0 || strcmp(symbolName, "v") == 0)
    {
        templates[0] = V_DATA_ZERO;
        templates[1] = V_DATA_ONE;
        templates[2] = V_DATA_TWO;
        templates[3] = V_DATA_THREE;
        templates[4] = V_ORIENT_UPRIGHT;
        templates[5] = V_ORIENT_UPLEFT;
        templates[6] = V_ORIENT_DOWNLEFT;
        templates[7] = V_ORIENT_DOWNRIGHT;
    }
    else
    {
        if (strcmp(symbolName, "Y") == 0 || strcmp(symbolName, "y") == 0)
        {
            templates[0] = Y_DATA_ZERO;
            templates[1] = Y_DATA_ONE;
            templates[2] = Y_DATA_TWO;
            templates[3] = Y_DATA_THREE;
            templates[4] = Y_ORIENT_UPRIGHT;
            templates[5] = Y_ORIENT_UPLEFT;
            templates[6] = Y_ORIENT_DOWNLEFT;
            templates[7] = Y_ORIENT_DOWNRIGHT;
        }
        else
        {
            templates[0] = Y_DATA_ZERO;
            templates[1] = Y_DATA_ONE;
            templates[2] = Y_DATA_TWO;
            templates[3] = Y_DATA_THREE;
            templates[4] = Y_ORIENT_UPRIGHT;
            templates[5] = Y_ORIENT_UPLEFT;
            templates[6] = Y_ORIENT_DOWNLEFT;
            templates[7] = Y_ORIENT_DOWNRIGHT;
        }
    }

    return templates;
}






/*!\fn int recognize_symbol_type(const CvMat *symbolImage, const float **templates, 
                                                        float brokenTypeThresh)
 * \param [in] symbolImage - image to recognize.
 * \param [in] templates - templates for specified symbol.
 * \param [in] brokenTypeThresh - thresh value for correlation 
 *                                to return broken type id.
 * \return 1 if symbol has data type, -1 if symbol has orientation type and 0
 *         if symbol has broken type.
 *
 * Determine the type of the given symbol. To do it calculate correlation for
 * all ideal symbol and find the nearest of them. If the maximal correlation
 * value is over the given threshold value the function returns the type 
 * which corresponds to the nearest symbol. Otherwise broken type id will be
 * returned.
 */
int recognize_symbol_type(const CvMat *symbolImage, const float **templates, 
                                                        float brokenTypeThresh)
{
    // allocate memory for data array
    float data[25];
    
    // get normalized features values from the image
    get_feature_vector_from_image(symbolImage, data);

    // calculate correlation coefficients.
    float corr_coef[8];
    corr_coef[DATA_ZERO_ID] = calc_correlation(data, templates[0], 25);
    corr_coef[DATA_ONE_ID] = calc_correlation(data, templates[1], 25);
    corr_coef[DATA_TWO_ID] = calc_correlation(data, templates[2], 25);
    corr_coef[DATA_THREE_ID] = calc_correlation(data, templates[3], 25);
    corr_coef[ORIENT_UPRIGHT_ID] = calc_correlation(data, templates[4], 25);
    corr_coef[ORIENT_UPLEFT_ID] = calc_correlation(data, templates[5], 25);
    corr_coef[ORIENT_DOWNLEFT_ID] = calc_correlation(data, templates[6], 25);
    corr_coef[ORIENT_DOWNRIGHT_ID] = calc_correlation(data, templates[7], 25);

    // find the largest correlation.
    float max = corr_coef[0];
    int indmax = 0;
    for (int i = 0; i < 8; i++)
        if (corr_coef[i] > max)
        {
            max = corr_coef[i];
            indmax = i;
        }

    //printf("max is %f\n", max);

    if (max < brokenTypeThresh)
        return 0;
    else
        return (indmax%2 == 0) ? 1 : -1;   
}


/*!\fn int recognize_data_symbol(const CvMat *symbolImage, const float **templates)
 * \param [in] symbolImage - image to recognize.
 * \param [in] templates - templates for specified symbol.
 * \return the integer id which corresponds to symbol.
 */
int recognize_data_symbol(const CvMat *symbolImage, const float **templates)
{
    // allocate memory for data array
    float data[25];
    
    // get normalized features values from the image
    get_feature_vector_from_image(symbolImage, data);

    // calculate correlation coefficients.
    float corr_coef[4];
    corr_coef[0] = calc_correlation(data, templates[0], 25);
    corr_coef[1] = calc_correlation(data, templates[1], 25);
    corr_coef[2] = calc_correlation(data, templates[2], 25);
    corr_coef[3] = calc_correlation(data, templates[3], 25);
    
    // find the largest correlation.
    float max = corr_coef[0];
    int indmax = 0;
    for (int i = 0; i < 4; i++)
        if (corr_coef[i] > max)
        {
            max = corr_coef[i];
            indmax = i;
        }
    
    int ret = -1;
    switch(indmax)
    {
        case 0:
            ret = DATA_ZERO_ID;
            break;
        case 1:
            ret = DATA_ONE_ID;
            break;
        case 2:
            ret = DATA_TWO_ID;
            break;
        case 3:
            ret = DATA_THREE_ID;
            break;
    }

    return ret;
}

/*!\fn recognize_orient_symbol(const CvMat *symbolImage, const float **templates)
 * \param [in] symbolImage - image to recognize.
 * \param [in] templates - templates for specified symbol.
 * \return the integer id which corresponds to symbol.
 */
int recognize_orient_symbol(const CvMat *symbolImage, const float **templates)
{
    // allocate memory for data array
    float data[25];
    
    // get normalized features values from the image
    get_feature_vector_from_image(symbolImage, data);
    
    // calculate correlation coefficients.
    float corr_coef[4];
    corr_coef[0] = calc_correlation(data, templates[4], 25);
    corr_coef[1] = calc_correlation(data, templates[5], 25);
    corr_coef[2] = calc_correlation(data, templates[6], 25);
    corr_coef[3] = calc_correlation(data, templates[7], 25);
    
    // find the largest correlation.
    float max = corr_coef[0];
    int indmax = 0;
    for (int i = 0; i < 4; i++)
        if (corr_coef[i] > max)
        {
            max = corr_coef[i];
            indmax = i;
        }
    
    int ret = -1;
    switch(indmax)
    {
        case 0:
            ret = ORIENT_UPRIGHT_ID;
            break;
        case 1:
            ret = ORIENT_UPLEFT_ID;
            break;
        case 2:
            ret = ORIENT_DOWNLEFT_ID;
            break;
        case 3:
            ret = ORIENT_DOWNRIGHT_ID;
            break;
    }

    return ret;
}


/*!\fn get_min_area(const CvMat *image, CvRect area)
 * \param [in] image - the source image.
 * \param [in] area - horizontally oriented rectangular area 
 *                    around the symbol on the image.
 *\return minimal rectangular area around the symbol.
 *
 * Calculate minimal area oriented rectangle around the non-zero
 * pixels in the subframe of the image.
 */
CvBox2D get_min_area(const CvMat *image, CvRect area)
{
    // get corresponding sub image (data isn't copied!!!) 
    CvMat subImage;// = cvCreateMatHeader(area.height, area.width, CV_8UC1); 
    cvGetSubRect(image, &subImage, area);
    
    //printf("Subrect is got!\n");

    // calculate the quantity of points and allocate corresponding memory
    int pointsQuan = (int)(cvSum(&subImage).val[0]/255);
    CvPoint *points = (CvPoint*)malloc(pointsQuan * sizeof(CvPoint));
    CvMat pointMat = cvMat(1, pointsQuan, CV_32SC2, points);
    
    //printf("Memory for all points is allocated!Points quantity is %d\n", pointsQuan);

    // fill allocated memory
    int count = 0;
    for (int i = 0; i < subImage.rows; i++)
        for (int j = 0; j < subImage.cols; j++)
            if (CV_MAT_ELEM(subImage, uchar, i, j) != 0)
            {
                points[count] = cvPoint(area.x + j, area.y + i);
                count++;
            }
    
     //printf("Memory is filled!Count is %d\n", count);

      
     // get rotated rectangle around
     CvBox2D box = cvMinAreaRect2(&pointMat, NULL);
     
     //printf("Min area rect is calculated!\n");

     // release allocated memory
     free(points);
     //free(subImage);

     // additional angle processing
#ifdef FULL_CV_DEBUG
     CvMat *canvas = cvCreateMat(image->rows, image->cols, CV_8UC3);
     cvCvtColor(image, canvas, CV_GRAY2BGR);
     CvPoint2D32f pts[4];
     cvBoxPoints(box, pts);
     cvCircle(canvas, cvPointFrom32f(pts[0]), 5, CV_RGB(255, 0, 0), 1, 8, 0);
     cvCircle(canvas, cvPointFrom32f(pts[1]), 5, CV_RGB(0, 255, 0), 1, 8, 0);
     cvCircle(canvas, cvPointFrom32f(pts[2]), 5, CV_RGB(0, 0, 255), 1, 8, 0);
     cvCircle(canvas, cvPointFrom32f(pts[3]), 5, CV_RGB(255, 0, 255), 1, 8, 0);
     cvShowImage("Box points", canvas);
     cvWaitKey(0);
     printf("Angle is %f.\nCenter is (%f, %f).\nSize is (%f,%f).\n",
        box.angle, box.center.x, box.center.y, box.size.width, box.size.height);
     //cvReleaseMat(&canvas);
     //CvBox2D anotherBox = box;
     //anotherBox.angle *= -1;
     //canvas = cvCreateMat(image->rows, image->cols, CV_8UC3);
     //cvCvtColor(image, canvas, CV_GRAY2BGR);
     //cvBoxPoints(anotherBox, pts);
     //cvCircle(canvas, cvPointFrom32f(pts[0]), 5, CV_RGB(255, 0, 0), 1, 8, 0);
     //cvCircle(canvas, cvPointFrom32f(pts[1]), 5, CV_RGB(0, 255, 0), 1, 8, 0);
     //cvCircle(canvas, cvPointFrom32f(pts[2]), 5, CV_RGB(0, 0, 255), 1, 8, 0);
     //cvCircle(canvas, cvPointFrom32f(pts[3]), 5, CV_RGB(255, 0, 255), 1, 8, 0);
     //cvShowImage("Another box points", canvas);
     //cvWaitKey(0);

#endif
     //float angle = box.angle;
     //if (angle > 45.)
     //    angle = (float)90 - angle;
     //box.angle = angle;
     //box.angle = (float)90 - angle;
     return box;
}


/*!\fn align_binary_image(const CvMat *image)
 * \param [in] markImage - input image.
 * \return pointer to the aligned image.
 *
 * Align binary image corresponding to the nearest straightforward
 * condition of the mark. To do it orientation info for whole mark
 * is extracted and image is rotated the estimated angle.
 */
AlignInfo align_binary_image(const CvMat *markImage)
{
    // output structure
    AlignInfo alignInfo;
    alignInfo.alignedImage = NULL;
    alignInfo.mapMatrix = NULL;
    alignInfo.alignAngle = -1;

    // get orientation information about binary mark
    CvBox2D box = get_min_area(markImage, 
                    cvRect(0,0,markImage->cols, markImage->rows));
    
#ifdef FULL_CV_DEBUG
    printf("Angle is %f.\nCenter is (%f, %f).\nSize is (%f,%f).\n",
        box.angle, box.center.x, box.center.y, box.size.width, box.size.height);
#endif
    
    // Calculate secondary component of rotation angle.
    alignInfo.alignAngle = (int)box.angle;
    
    // Allocate memory for output aligned image
    int a = markImage->rows;//height of the source image
    int b = markImage->cols;//width of the source image
    //float radAngle = (float)(box.angle * 3.14159 / 180.);//orientation angle in radians
    int aRot = (int)(box.size.height);
    int bRot = (int)(box.size.width);
    alignInfo.alignedImage = cvCreateMat(aRot, bRot, markImage->type);
    
    // create affine transformation matrix
    alignInfo.mapMatrix = cvCreateMat(2, 3, CV_32FC1);// allocate memory
    cv2DRotationMatrix(box.center, (float)90 - box.angle, 1., alignInfo.mapMatrix);// calculate matrix
    // change matrix values corresponding to shift of image center
    CV_MAT_ELEM(*(alignInfo.mapMatrix), float, 0, 2) = 
                        CV_MAT_ELEM(*(alignInfo.mapMatrix), float, 0, 2) + (float)(bRot/2. - box.center.x);
    CV_MAT_ELEM(*(alignInfo.mapMatrix), float, 1, 2) = 
                        CV_MAT_ELEM(*(alignInfo.mapMatrix), float, 1, 2) + (float)(aRot/2. - box.center.y);
    
    // apply affine transform
    cvWarpAffine(markImage, alignInfo.alignedImage, alignInfo.mapMatrix,
                CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS, cvScalarAll(0));

#ifdef FULL_CV_DEBUG
    printf("Number of white pixels in the input image is %f.\n", cvSum(markImage).val[0]/255.);
    printf("Number of white pixels in the output image is %f.\n", cvSum(alignInfo.alignedImage).val[0]/255.);
#endif

    return alignInfo;
}


/*! \fn invert_image(const CvMat *source)
 * It works only for CV_8UC1 images.
 * \param [in] source - image to invert.
 * \return inverted image.
 */
CvMat *invert_image(const CvMat *source)
{
    // If source's type is not CV_8UC1 return NULL
    if (CV_MAT_TYPE(source->type) != CV_8UC1)
        return NULL;
    
    // create inverter matrix
    CvMat *inverter = cvCreateMat(source->rows, source->cols, source->type);
    cvSet(inverter, cvScalar(255, 0, 0, 0), NULL);

    // create result matrix
    CvMat *result = cvCreateMat(source->rows, source->cols, source->type);

    // do matrix operation
    cvScaleAdd(source, cvScalar(-1, 0, 0, 0), inverter, result);

    cvReleaseMat(&inverter);
    return result;
}


/*! \fn find_focus_on_partial_matrix(int **partialMatrix, int rows, int cols, int numThresh)
 * \param [in] partialMatrix - 2D integer matrix of mark labels 
 *                                      to find orientation focus.
 * \param [in] rows - number of rows in the input matrix.
 * \param [in] cols - number of columns in the input matrix.
 * \param [in] numThresh - threshol value for number of symbols recognized in the row or column as oriented.
 * \return CvPoint with integer coordinates of focus in the matrix.
 * If focus isn't able to be found, cvPoint(-1, -1) will be returned.
 */
CvPoint find_focus_on_partial_matrix(int **partialMatrix, int rows, int cols, int numThresh)
{
    // find focus coordinates
    int iFocus = -1;
    int jFocus = -1;

    int maxNum = numThresh - 1;// it must be more than numThresh - 1 orientation symbol in the row
    for (int i = 0; i < rows; i++)
    {
        int count = 0;

        for (int j = 0; j < cols; j++)
            if (partialMatrix[i][j] == -1)
                count++;

        if (count > maxNum)
        {
            maxNum = count;
            iFocus = i;
        }
    }

#ifdef FULL_CV_DEBUG
	printf("Orientation row is %d. It was found %d orientation symbols there.\n", iFocus, maxNum); 
#endif

    maxNum = numThresh - 1;// it must be more than numThresh - 1 orientation symbol in the column
    for (int j = 0; j < cols; j++)
    {
        int count = 0;

        for (int i = 0; i < rows; i++)
            if (partialMatrix[i][j] == -1)
                count++;

        if (count > maxNum)
        {
            maxNum = count;
            jFocus = j;
        }
    }

#ifdef FULL_CV_DEBUG
	printf("Orientation column is %d. It was found %d orientation symbols there.\n", jFocus, maxNum); 
#endif

    if (jFocus == -1 || iFocus == -1)
        return cvPoint(-1, -1);

    return cvPoint(jFocus, iFocus);
}


/*!\fn binarize_image(const CvMat *in, int blockSize, double shiftValue,
                      double minBlobSize, double maxBlobSize, 
                      double maxBlobSideRatio, double minBlobDensity,
                      double maxBlobDensity, double maxGeomRegParam)
*\param [in] in - input grayscale image in the form of CV_8UC1 matrix.
*\param [in] blockSize - segmentation block size value.
*\param [in] shiftValue - segmentation shift value.
*\param [in] minBlobSize - minimal blob size value.
*\param [in] maxBlobSize - maximal blob size value.
*\param [in] maxBlobSideRatio - maximal sides ratio for blob.
*\param [in] minBlobDensity - minimal blob density value.
*\param [in] maxBlobDensity - maximal blob density value.
*\param [in] maxGeomRegParam - maximal geometry regularity parameter.
*\return pointer to the output CvMat image or NULL in the error case.
*
* Binarize input gray scale image using adaptive thresholding. After that
* find blobs and filter that which are not-suitable for following recognition.
* Than draw remained blobs and return new image.
*/
CvMat *binarize_image(const CvMat *in, int blockSize, double shiftValue,
                      double minBlobSize, double maxBlobSize, 
                      double maxBlobSideRatio, double minBlobDensity,
                      double maxBlobDensity, double maxGeomRegParam)
{
    if (in == NULL || in->rows < 1 || in->cols < 1 || CV_MAT_TYPE(in->type) != CV_8UC1)
        return NULL;//INCORRECT INPUT PARAMETER ERROR
    
#ifdef FULL_CV_DEBUG
	printf("Start to binarize image...\n");
#endif

    int h = in->rows;
    int w = in->cols;

    // adaptively threshold input gray-scale image
    CvMat *bin = cvCreateMat(h, w, CV_8UC1);
    cvAdaptiveThreshold(in, bin, 255, CV_ADAPTIVE_THRESH_MEAN_C,
                        CV_THRESH_BINARY, blockSize, shiftValue);
#ifdef FULL_CV_DEBUG
	cvShowImage("Adaptively binarized image", bin);
	cvWaitKey(0);
#endif
    
    //printf("Number of white pixels is %f\n", cvSum(bin).val[0]/255);
    
    // find blobs on the binary image
    BlobArray blobArray = find_blobs(bin);

#ifdef FULL_CV_DEBUG
	CvMat *canvas = cvCloneMat(in);
	cvSet(canvas, cvScalar(0, 0, 0, 0), NULL);
	draw_blobs(blobArray, canvas);
	cvShowImage("After blobs are found", canvas);
	cvWaitKey(0);
	cvReleaseMat(&canvas);
#endif

    //printf("Blobs are found\n");
    // filter found blobs
    filter_blobs_with_size(&blobArray, minBlobSize, maxBlobSize);
#ifdef FULL_CV_DEBUG
	canvas = cvCloneMat(in);
	cvSet(canvas, cvScalar(0, 0, 0, 0), NULL);
	draw_blobs(blobArray, canvas);
	cvShowImage("After filtering blobs with size", canvas);
	cvWaitKey(0);
	cvReleaseMat(&canvas);
#endif
    filter_blobs_with_side_ratio(&blobArray, maxBlobSideRatio);
#ifdef FULL_CV_DEBUG
	canvas = cvCloneMat(in);
	cvSet(canvas, cvScalar(0, 0, 0, 0), NULL);
	draw_blobs(blobArray, canvas);
	cvShowImage("After filtering blobs with side ratio", canvas);
	cvWaitKey(0);
	cvReleaseMat(&canvas);
#endif
    filter_blobs_with_density(&blobArray, minBlobDensity, maxBlobDensity);
#ifdef FULL_CV_DEBUG
	canvas = cvCloneMat(in);
	cvSet(canvas, cvScalar(0, 0, 0, 0), NULL);
	draw_blobs(blobArray, canvas);
	cvShowImage("After filtering blobs with density", canvas);
	cvWaitKey(0);
	cvReleaseMat(&canvas);
#endif
    filter_blobs_with_geometry_regularity(&blobArray, maxGeomRegParam);
#ifdef FULL_CV_DEBUG
	canvas = cvCloneMat(in);
	cvSet(canvas, cvScalar(0, 0, 0, 0), NULL);
	draw_blobs(blobArray, canvas);
	cvShowImage("After filtering blobs with geometry regularity", canvas);
	cvWaitKey(0);
	cvReleaseMat(&canvas);
#endif
    // draw residual blobs on the output image
    CvMat *out = cvCreateMat(h, w, CV_8UC1);
    cvSet(out, cvScalar(0, 0, 0, 0), NULL);
    draw_blobs(blobArray, out);
    
    // release allocated memory
    cvReleaseMat(&bin);
    release_blob_array(&blobArray);
    
#ifdef FULL_CV_DEBUG
	printf("Number of white pixels is %f\n", cvSum(out).val[0]/255);
	printf("Image is successfully binarized...\n");
#endif
    return out;
}


/*! \fn smart_arrange_blobs(BlobArray blAr, int *rows, int *cols, double arrangeThresh)
 * \param [in] blAr - array of blobs to arrange.
 * \param [out] rows - number of blob rows after arrangement.
 * \param [out] cols - number of blob columns after arrangement.
 * \param [in] arrangeThresh - threshold value for arrangement.
 * \return array of CvPoints with coordinates after arrangement for each blob.
 *
 * Arrange blobs horizontally and vertically and determine the number of rows and columns.
 * First, for each pair of blobs find the degrees that they are left, right, top or down
 * neighbours. Second, for each blob find the nearest left, right, top and down neighbour.
 * Third, look for bijections in the pair "left-right" and "top-down" and set the
 * corresponding connections between blobs. After that look for additional connections
 * (for details look at "Blob.c": look_for_connections() function). And the final stage
 * is get 2D indexes for each blob.
 */
CvPoint *smart_arrange_blobs(BlobArray blAr, int *rows, int *cols,
                                                        double arrangeThresh)
{
    //printf("Begin smart arrange blobs\n");
    //const double arrangeThresh = -0.1;//0.0001
    int sz = blAr.sz;
    
    // allocate memory for neighbouring matrices
    double **leftN = allocate_2d_double_matrix(sz, sz, -DBL_MAX);
    double **rightN = allocate_2d_double_matrix(sz, sz, -DBL_MAX);
    double **topN = allocate_2d_double_matrix(sz, sz, -DBL_MAX);
    double **downN = allocate_2d_double_matrix(sz, sz, -DBL_MAX);

    // fill neighbouring matrices
    for (int i = 0; i < sz; i++)
        for (int j = i+1; j < sz; j++)
        {
            leftN[i][j] = isLeftNeighbour(blAr.blobs[i], blAr.blobs[j]);
            rightN[j][i] = leftN[i][j];
            leftN[j][i]= isRightNeighbour(blAr.blobs[i], blAr.blobs[j]);
            rightN[i][j] = leftN[j][i];
            topN[i][j] = isTopNeighbour(blAr.blobs[i], blAr.blobs[j]);
            downN[j][i] = topN[i][j];
            topN[j][i] = isDownNeighbour(blAr.blobs[i], blAr.blobs[j]);
            downN[i][j] = topN[j][i];
        }
    
    // allocate memory for neighbouring vector
    int *left = (int*)malloc(sz * sizeof(int));
    int *right = (int*)malloc(sz * sizeof(int));
    int *top = (int*)malloc(sz * sizeof(int));
    int *down = (int*)malloc(sz * sizeof(int));

    // fill neighbouring vectors finding maximums per row in neighbouring matrices
    for (int i = 0; i < sz; i++)
    {
        double max;
        int maxInd;
        
        maximum_double_1d(leftN[i], sz, &max, &maxInd);
        left[i] = (max > arrangeThresh) ? maxInd : -1;
        maximum_double_1d(rightN[i], sz, &max, &maxInd);
        right[i] = (max > arrangeThresh) ? maxInd : -1;
        maximum_double_1d(topN[i], sz, &max, &maxInd);
        top[i] = (max > arrangeThresh) ? maxInd : -1;
        maximum_double_1d(downN[i], sz, &max, &maxInd);
        down[i] = (max > arrangeThresh) ? maxInd : -1;
    }
    
    // validate neighbouring : looking for bijections  
    for (int i = 0; i < sz; i++)
    {
        if (left[i] != -1)
            if (right[left[i]] != i)
                left[i] = -1;

        if (right[i] != -1)
            if (left[right[i]] != i)
                right[i] = -1;

        if (top[i] != -1)
            if (down[top[i]] != i)
                top[i] = -1;

        if (down[i] != -1)
            if (top[down[i]] != i)
                down[i] = -1;
    }

    //look for pairs <Bi, Bj>, where Bi has no left neighbour,
    //and Bj has no right neighbour, try to connect them if corresponding
    //binary relations admit. The same for top-down pairs.
    
    for (int i = 0; i < sz; i++)
    {
        //printf("here 1\n");
        left[i] = (left[i] == -1) ? 
                    look_for_connections(isLeftNeighbour, 
                        right, blAr, i, arrangeThresh) : left[i];
        //printf("here 2\n");
        right[i] = (right[i] == -1) ? 
                    look_for_connections(isRightNeighbour,
                        left, blAr, i, arrangeThresh) : right[i];

        //printf("here 3\n");
        top[i] = (top[i] == -1) ? 
                    look_for_connections(isTopNeighbour,
                            down, blAr, i, arrangeThresh) : top[i];

        //printf("here 4\n");
        down[i] = (down[i] == -1) ? 
                    look_for_connections(isDownNeighbour,
                            top, blAr, i, arrangeThresh) : down[i];
    }

    // free allocated memory for neighbouring matrices
    free_2d_double_matrix(leftN, sz);
    free_2d_double_matrix(rightN, sz);
    free_2d_double_matrix(topN, sz);
    free_2d_double_matrix(downN, sz);

    //printf("Neighbours are calculated\n");

    // Calculate horizontal indexes of blob lines
    double *coordinates = (double*)malloc(sz * sizeof(double));
    for (int i = 0; i < sz; i++)
        coordinates[i] = blAr.blobs[i].mrarea.center.y;
    
    int *horLineIds = (int *)malloc(sz * sizeof(int));
    
    //printf("Before horizontal indexes are calculated\n");
    *rows = get_line_ids(left, coordinates, horLineIds, sz);
    

    // Calculate vertical indexes of blob lines
    for (int i = 0; i < sz; i++)
        coordinates[i] = blAr.blobs[i].mrarea.center.x;

    int *verLineIds = (int*)malloc(sz * sizeof(int));

    *cols = get_line_ids(top, coordinates, verLineIds, sz);
    
    //printf("Indexes for lines are calculated\n");

    // grid indexes for blobs 
    CvPoint *indexes = (CvPoint*)malloc(sz * sizeof(CvPoint));
    for (int i = 0; i < sz; i++)
    {
        indexes[i].x = verLineIds[i];
        indexes[i].y = horLineIds[i];
    }

    // free allocated memory for neighbouring vectors
    free(left);
    free(right);
    free(top);
    free(down);

    // free other allocated memory
    free(coordinates);
    free(horLineIds);
    free(verLineIds);

    return indexes;
}





/*!\fn translate_blobs_to_mark(BlobArray blAr, Context *cxt)
 * \param [in] blAr - input array of blobs.
 * \param [in] cxt - input context.
 * \return recognition mark results structure.
 *
 * Translate given blob array to the recognized mark.
 * First, arrange blobs with smart_arrange_blobs() function while
 * it doesn't satisfy to format or number of estimated rows and columns
 * is changing receiving in the common case partial matrix of index elements.
 * Second, find the focus point in this partial matrix. And correspondingly
 * the found focus build full (height+1 x width+1) matrix in the center
 * with the focus point. Offset between focus point in partial and full
 * matrices is applied to all points in the partial matrix to copy them into
 * full matrix. After that the main component of rotation angle is determined
 * per all crosshair symbols recognition. Also all data symbols are recognized.
 * Then correspondingly to determined angle all full matrix is rotated and all
 * data symbols values are updated.If it needs calculate addtional diagnostic
 * information.
 */
RecMark translate_blobs_to_mark(BlobArray blAr, Context *cxt)
{
    
    int markWidth = cxt->width;
    int markHeight = cxt->height;
#ifdef FULL_CV_DEBUG
    printf("markHeight = %d markWidth = %d\n", markHeight, markWidth);
#endif
    double criticalNgbAreaRatio = cxt->algSettings.criticalNgbAreaRatio;
    double badSymbolRecThresh = cxt->algSettings.badSymbolRecThresh;

    // init output structure
    RecMark recMark;
    // main info
    recMark.symbols = blAr;
    recMark.width = markWidth;
    recMark.height = markHeight;
    recMark.indexes = NULL;
    recMark.result = NULL;
    recMark.error = 0;

#ifdef CALC_DIAGNOSTIC_INFO
    // additional info
    recMark.values = NULL;
    recMark.types = NULL;
    recMark.focus = cvPoint(-1, -1);
    recMark.upperLeft = NULL;
    recMark.lowerRight = NULL;
    recMark.angle = -1;
#endif
    

    if (markWidth%2 == 1 || markHeight%2 == 1 || markHeight <= 0 || markWidth <= 0)
    {
        recMark.error = 1;

#ifdef CALC_DIAGNOSTIC_INFO
        calculate_mark_additional_info(&recMark);
#endif

        return recMark;// INCORRECT INPUT DATA ERROR:
        // mark's width and height should be even positive numbers
    }
    
    // rows and columns number to calculate
    int rows = 0;
    int cols = 0;
     
    // arrange blobs, determine number of visible rows and columns on the mark
    double arrangeThresh = cxt->algSettings.arrangeThresh; 
    CvPoint *indexes = smart_arrange_blobs(blAr, &rows, &cols, arrangeThresh);
#ifdef FULL_CV_DEBUG
    printf("rows = %d cols = %d\n", rows, cols);
#endif
    while(1)
    {
        int oldRows = rows, oldCols = cols;
        filter_blobs_on_grid(&blAr, indexes, rows, cols, criticalNgbAreaRatio);
        free(indexes);
        indexes = smart_arrange_blobs(blAr, &rows, &cols, arrangeThresh);
#ifdef FULL_CV_DEBUG
        printf("rows = %d cols = %d\n", rows, cols);
#endif
        if (rows <= markHeight + 1 && cols <= markWidth + 1)
            break;

        if (oldRows == rows && oldCols == cols)
            break;
    }
    
    //CvPoint *indexes = smart_arrange_blobs(blAr, &rows, &cols);
    //filter_blobs_on_grid(&blAr, indexes, rows, cols, criticalNgbAreaRatio);
    //free(indexes);
    //indexes = smart_arrange_blobs(blAr, &rows, &cols);
    //filter_blobs_on_grid(&blAr, indexes, rows, cols, criticalNgbAreaRatio);
    //free(indexes);
    //indexes = smart_arrange_blobs(blAr, &rows, &cols);

#ifdef FULL_CV_DEBUG
	printf("rows = %d, cols = %d blobsNum = %d \n", rows, cols, blAr.sz);
	/*draw rows and cols for debug*/
	srand((unsigned int)time(NULL));
	CvMat *rCanvas = cvCreateMat(800, 800, CV_8UC3);
	CvMat *cCanvas = cvCreateMat(800, 800, CV_8UC3);
    CvMat *canvas = cvCreateMat(800, 800, CV_8UC3);
	int colorNum = rows > cols ? rows : cols;
	CvScalar *colors = (CvScalar*)malloc(colorNum * sizeof(CvScalar));
	for (int i = 0; i < colorNum; i++)
		colors[i] = CV_RGB(rand()%256, rand()%256, rand()%256);
	for (unsigned i = 0; i < blAr.sz; i++)
	{
		CvRect r = blAr.blobs[i].frarea;
		CvMat *rSubMat = cvCreateMatHeader(r.height, r.width, CV_8UC3);
		CvMat *cSubMat = cvCreateMatHeader(r.height, r.width, CV_8UC3);
        CvMat *subMat = cvCreateMatHeader(r.height, r.width, CV_8UC3);
		cvGetSubRect(rCanvas, rSubMat, r);
		cvGetSubRect(cCanvas, cSubMat, r);
        cvGetSubRect(canvas, subMat, r);
		cvSet(rSubMat, colors[indexes[i].y],  blAr.blobs[i].mask);
		cvSet(cSubMat, colors[indexes[i].x],  blAr.blobs[i].mask);
        cvSet(subMat, CV_RGB(rand()%256, rand()%256, rand()%256), blAr.blobs[i].mask);
	}
	cvShowImage("Rows", rCanvas);
	cvShowImage("Columns", cCanvas);
    cvShowImage("Symbols", canvas);
	cvWaitKey(0);
	cvReleaseMat(&rCanvas);
	cvReleaseMat(&cCanvas);
    cvReleaseMat(&canvas);
	/********************/
#endif

    recMark.symbols = blAr;

    // get templates which correspond to specified symbol
    float **symbolTemplates = get_symbol_templates(cxt->symbolName);

    // allocate memory for partial matrices
    int **partialMatrix = allocate_2d_int_matrix(rows, cols, 0);
    int **indexPartialMatrix = allocate_2d_int_matrix(rows, cols, -1);

    // fill partial matrix
    for (unsigned i = 0; i < blAr.sz; i++)
    {
        // recognize type of symbol
        partialMatrix[indexes[i].y][indexes[i].x] = 
                        recognize_symbol_type(blAr.blobs[i].mask,
                                              (const float **)symbolTemplates,
                                                (float)badSymbolRecThresh);
        // save blob id
        indexPartialMatrix[indexes[i].y][indexes[i].x] = i;
    }

    // try to find focus
    CvPoint focusPoint = 
		find_focus_on_partial_matrix(partialMatrix, rows, cols, cxt->algSettings.findFocusNumThresh);

#ifdef FULL_CV_DEBUG
    printf("Partial focus is (%d, %d).\n", focusPoint.x, focusPoint.y);
#endif
    
    if (focusPoint.x == -1)
    {
        free(indexes);
        free_2d_int_matrix(partialMatrix, rows);
        free_2d_int_matrix(indexPartialMatrix, rows);
        
        recMark.error = 2;

#ifdef CALC_DIAGNOSTIC_INFO
        calculate_mark_additional_info(&recMark);
#endif

        return recMark; // FOCUS NOT FOUND ERROR
    }

    // fill partial matrix with recognition results
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
            partialMatrix[i][j] = -1;
    

    int rotations[4] = {0, 0, 0, 0};
    for (unsigned i = 0; i < blAr.sz; i++)
        if (indexes[i].y == focusPoint.y || indexes[i].x == focusPoint.x)
        {
            int symbol = recognize_orient_symbol(blAr.blobs[i].mask,
                                                 (const float **)symbolTemplates);
            rotations[symbol/2]++;
        }
        else
            partialMatrix[indexes[i].y][indexes[i].x] = 
                          recognize_data_symbol(blAr.blobs[i].mask,
                                                (const float **)symbolTemplates)/2;
             
    free(indexes);
    
    // free allocated templates
    free(symbolTemplates);

    // determine rotate angle
    int maxNum = 0;
    int rotId = -1;
    maximum_int_1d(rotations, 4, &maxNum, &rotId);
    int rotateCommand = 6 - 2*rotId;

#ifdef CALC_DIAGNOSTIC_INFO
// calculate main component of diagnostic angle
    switch (rotateCommand)
    {
        case 0:
            recMark.angle = 90;
            break;
        case 2:
            recMark.angle = 0;
            break;
        case 4:
            recMark.angle = 270;
            break;
        case 6:
            recMark.angle = 180;
            break;
    }
#endif
    
    int  _Width = (rotateCommand == 0 || rotateCommand == 4) ? markWidth : markHeight;
	int _Height = (rotateCommand == 0 || rotateCommand == 4) ? markHeight : markWidth;
    
    if (rows > _Height + 1 || cols > _Width + 1)
    {
        free_2d_int_matrix(partialMatrix, rows);
        free_2d_int_matrix(indexPartialMatrix, rows);
        recMark.error = 3;

#ifdef CALC_DIAGNOSTIC_INFO
        calculate_mark_additional_info(&recMark);
#endif

        return recMark;//RECOGNIZED MARK DOESN'T SATISFY TO FORMAT
  
    }

    // find offset for focus
    CvPoint focusOffset = cvPoint(_Width/2 - focusPoint.x,
                                  _Height/2 - focusPoint.y);

#ifdef FULL_CV_DEBUG
	printf("Focus offset is (%d, %d)\n", focusOffset.x, focusOffset.y);
#endif
    if (focusOffset.x < 0 || focusOffset.y < 0 || 
        focusOffset.x > _Width + 1 - cols || 
        focusOffset.y > _Height + 1 - rows)
    {
        free_2d_int_matrix(partialMatrix, rows);
        free_2d_int_matrix(indexPartialMatrix, rows);
        recMark.error = 3;

#ifdef CALC_DIAGNOSTIC_INFO
        calculate_mark_additional_info(&recMark);
#endif
        return recMark; // RECOGNIZED MARK DOESN'T SATISFY TO FORMAT
    }

#ifdef FULL_CV_DEBUG
    printf("markHeight = %d markWidth = %d\n", markHeight, markWidth);
    printf("rows = %d cols = %d\n", rows, cols);
#endif    
    
    // allocate memory for output data
    recMark.result = allocate_2d_int_matrix(_Height, _Width, -1);
    recMark.indexes = allocate_2d_int_matrix(_Height + 1, _Width + 1, -1);

#ifdef FULL_CV_DEBUG
    printf("Memory is allocated: number of rows is %d, number of cols is %d\n", _Height, _Width);
    printf("Rotate command is %d\n", rotateCommand);
#endif

#ifdef FULL_CV_DEBUG
    printf("Partial matrix is:\n");   
    for (int i = 0; i < rows; i++)
    {
        for (int j = 0; j < cols; j++)
            printf("%4d ", partialMatrix[i][j]);

        printf("\n");
    }

#endif    
    
    // copy data from partial matrix to output matrix
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
        {
            // calculate coordinates taking into account focus offset
            int newI = i + focusOffset.y;
            int newJ = j + focusOffset.x;

            // if symbol belongs to orientation crosshair skip it
            //if (newI == markHeight/2 || newJ == markWidth/2)
			if (newI == _Height/2 || newJ == _Width/2)
                continue;
            
            // calculate coordinates without orientation symbols
            //int realI = (newI < markHeight/2) ? newI : newI - 1;
            //int realJ = (newJ < markWidth/2) ? newJ : newJ - 1;
			int realI = (newI < _Height/2) ? newI : newI - 1;
            int realJ = (newJ < _Width/2) ? newJ : newJ - 1;
            
            recMark.result[realI][realJ] = partialMatrix[i][j];
        }

#ifdef FULL_CV_DEBUG
    printf("After assignment matrix is:\n");   
    for (int i = 0; i < _Height; i++)
    {
        for (int j = 0; j < _Width; j++)
            printf("%4d ", recMark.result[i][j]);

        printf("\n");
    }

#endif

#ifdef FULL_CV_DEBUG
	printf("Result data is copied\n");
#endif

#ifdef FULL_CV_DEBUG
    printf("Index partial matrix is:\n");   
    for (int i = 0; i < rows; i++)
    {
        for (int j = 0; j < cols; j++)
            printf("%4d ", indexPartialMatrix[i][j]);

        printf("\n");
    }

#endif
    
    // copy data from index partial matrix to index output matrix
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
        {
            // calculate coordinates taking into account focus offset
            int newI = i + focusOffset.y;
            int newJ = j + focusOffset.x;

            recMark.indexes[newI][newJ] = indexPartialMatrix[i][j];
        }

#ifdef FULL_CV_DEBUG
    printf("After assignment indexes matrix is:\n");   
    for (int i = 0; i < _Height + 1; i++)
    {
        for (int j = 0; j < _Width + 1; j++)
            printf("%4d ", recMark.indexes[i][j]);

        printf("\n");
    }
#endif

#ifdef FULL_CV_DEBUG
	printf("Index data is copied 1\n");
#endif

    // free partial matrix
    free_2d_int_matrix(indexPartialMatrix, rows);
    free_2d_int_matrix(partialMatrix, rows);

#ifdef FULL_CV_DEBUG
	printf("Partial data is free \n");
    printf("Rotate command / 2 is %d.\n", rotateCommand/2); 
#endif
        
    // rotate output matrix
    int w = _Width, h = _Height;
	//int w = _Width, h = _Height;
    for (int i = 0; i < rotateCommand/2; i++)
    {
        rotate_matrix_90_degrees(&recMark.result, w, h);
        rotate_matrix_90_degrees(&recMark.indexes, w + 1, h + 1);
        int buf = w;
        w = h;
        h = buf;
    }

#ifdef FULL_CV_DEBUG
    printf("After rotations matrix is:\n");   
    for (int i = 0; i < h; i++)
    {
        for (int j = 0; j < w; j++)
            printf("%4d ", recMark.result[i][j]);

        printf("\n");
    }

#endif

#ifdef FULL_CV_DEBUG
    printf("After rotations indexes matrix is:\n");   
    for (int i = 0; i < h + 1; i++)
    {
        for (int j = 0; j < w + 1; j++)
            printf("%4d ", recMark.indexes[i][j]);

        printf("\n");
    }

#endif

#ifdef FULL_CV_DEBUG
	printf("Result data is rotated\n");
#endif

    update_matrix_values_after_rotation(recMark.result, h, w, rotateCommand/2);

#ifdef FULL_CV_DEBUG
    printf("After updating matrix is:\n");   
    for (int i = 0; i < h; i++)
    {
        for (int j = 0; j < w; j++)
            printf("%3d ", recMark.result[i][j]);

        printf("\n");
    }
#endif

#ifdef FULL_CV_DEBUG
	printf("Result values are updated\n");
#endif

    recMark.error = 0;

#ifdef CALC_DIAGNOSTIC_INFO
    // if needed calculate additional diagnostic info
    calculate_mark_additional_info(&recMark);
#endif

    return recMark;
}


#ifdef CALC_DIAGNOSTIC_INFO
/*! \fn calculate_mark_additional_info(RecMark *recMark)
 * \param [in,out] recMark - recognition mark info structure.
 */
void calculate_mark_additional_info(RecMark *recMark)
{
    // aliases
    int h = recMark->height;
    int w = recMark->width;
    int sz = recMark->symbols.sz;

    // fill diagnostic arrays of values and types
    recMark->values = (int*)malloc(sz * sizeof(int));
    recMark->types = (bool*)malloc(sz * sizeof(bool));

    if (recMark->error != 0)
    {
        for (int i = 0; i < sz; i++)
        {
            recMark->values[i] = -1;
            recMark->types[i] = false;
        }
    }
    else
        for (int i = 0; i < h + 1; i++)
            for (int j = 0; j < w + 1; j++)
            {
                // get index of blob
                int ind = recMark->indexes[i][j];
                // if blob exists (index != -1)
                if (ind != -1)
                {
                    // if blob is oriented symbol
                    if (i == h/2 || j == w/2)
                    {
                        recMark->types[ind] = false;

                        recMark->values[ind] = 0;
                    }
                    // if blob is data symbol
                    else
                    {
                        recMark->types[ind] = true;
                        
                        int vi = i < h/2 ? i : i-1;
                        int vj = j < w/2  ? j : j-1;
                        recMark->values[ind] = recMark->result[vi][vj];
                    }
                }
            }
    
    // calculate upper left and lower right points
    recMark->upperLeft = (CvPoint*)malloc(sz * sizeof(CvPoint));
    recMark->lowerRight = (CvPoint*)malloc(sz * sizeof(CvPoint));
    for (int i = 0; i < sz; i++)
    {
        CvRect r = recMark->symbols.blobs[i].frarea;
        recMark->upperLeft[i] = cvPoint(r.x, r.y);
        recMark->lowerRight[i] = cvPoint(r.x + r.width, r.y + r.height);
    }
    
    if (recMark->error != 0)
        recMark->focus = cvPoint(-1, -1);
    else
    {     
        // calculate focus point
        // if central symbol of mark was found
        int focusInd = recMark->indexes[h/2][w/2];
        if (focusInd != -1)
        {
#ifdef FULL_CV_DEBUG
            printf("Focus symbol is presented on the image.\n");
#endif
            CvPoint2D32f _focus = recMark->symbols.blobs[focusInd].mrarea.center;
            recMark->focus = cvPoint((int)_focus.x, (int)_focus.y);
        }
        // otherwise find crosshair lines equations and their intersection as focus
        else
        {
#ifdef FULL_CV_DEBUG
            printf("Focus symbol is not presented on the image.\n");
            CvMat *intersectionCanvas = cvCreateMat(500, 500, CV_8UC3);
            cvSet(intersectionCanvas, cvScalar(0, 0, 0, 0), NULL);
#endif
            float *x1 = (float*)malloc((h + 1)*sizeof(float));
            float *y1 = (float*)malloc((h + 1)*sizeof(float));
            int sz = 0;
            for (int i = 0; i < h + 1; i++)
            {
                int ind = recMark->indexes[i][w/2];
                if (ind != -1)
                {
                    sz++;
                    CvPoint2D32f p = recMark->symbols.blobs[ind].mrarea.center;
#ifdef FULL_CV_DEBUG
                    cvCircle(intersectionCanvas, cvPoint((int)p.x, (int)p.y),
                        5, CV_RGB(0, 255, 0), 1, 8, 0);
#endif
                    x1[sz - 1] = p.x;
                    y1[sz - 1] = p.y;
                }
            }
            
            Line line1 = get_line_equation(x1, y1, sz);
#ifdef FULL_CV_DEBUG
            cvLine(intersectionCanvas, 
            cvPoint((int)(line1.px + 100*line1.vx),((int)(line1.py + 100*line1.vy))),
            cvPoint((int)(line1.px - 100*line1.vx),((int)(line1.py - 100*line1.vy))),
            CV_RGB(0, 255, 0), 1, 8, 0);
#endif			
			free(x1);
			free(y1);

            float *x2 = (float*)malloc((w + 1)*sizeof(float));
            float *y2 = (float*)malloc((w + 1)*sizeof(float));
            sz = 0;
            for (int j = 0; j < w + 1; j++)
            {
                int ind = recMark->indexes[h/2][j];
                if (ind != -1)
                {
                    sz++;
                    CvPoint2D32f p = recMark->symbols.blobs[ind].mrarea.center;
#ifdef FULL_CV_DEBUG
                    cvCircle(intersectionCanvas, cvPoint((int)p.x, (int)p.y),
                        5, CV_RGB(0, 0, 255), 1, 8, 0);
#endif
                    x2[sz - 1] = p.x;
                    y2[sz - 1] = p.y;
                }
            }
            
            Line line2 = get_line_equation(x2, y2, sz);
#ifdef FULL_CV_DEBUG
            cvLine(intersectionCanvas, 
            cvPoint((int)(line2.px + 100*line2.vx),((int)(line2.py + 100*line2.vy))),
            cvPoint((int)(line2.px - 100*line2.vx),((int)(line2.py - 100*line2.vy))),
            CV_RGB(0, 0, 255), 1, 8, 0);
#endif
			free(x2);
			free(y2);

            CvPoint2D32f *p = get_lines_intersection(line1, line2, (float)0.01);
            
            if (p != NULL)
            {
#ifdef FULL_CV_DEBUG
                printf("Focus point is (%f, %f).\n", p->x, p->y);
                cvCircle(intersectionCanvas, cvPoint((int)p->x, (int)p->y),
                        5, CV_RGB(255, 0, 0), 1, 8, 0);
                cvShowImage("Intersection", intersectionCanvas);
                cvWaitKey(0);
                cvReleaseMat(&intersectionCanvas);
#endif
                recMark->focus = cvPoint((int)p->x, (int)p->y);
                free(p);
            }
        }
    }
}
#endif
