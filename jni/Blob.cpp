#include "Blob.h"

/*!\fn find_blobs(const CvMat *image)
 * \param [in] image - binary image to find blobs on.
 * \return array of blobs.
 * 
 * Finds connected components aka Blob varaibles on the input binary image and
 * returns the corresponding BlobArray. Input image should be 8 bit single channel.
 * All non-zero elements of this image will be treated as the parts of future
 * connected components to find.
 */
BlobArray find_blobs(const CvMat *image)
{
    // Source image will be modified in cvFindContours()
    CvMat *clonedImage = cvCloneMat(image);

    // find contours with two-level hierarchy
    CvMemStorage *memoryStorage = cvCreateMemStorage(0);
    CvSeq *contours = NULL;
    cvFindContours(clonedImage, memoryStorage, &contours, sizeof(CvContour), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
    
    int numContours = 0;
    Blob* blobs = NULL;

    while (contours != NULL)
    {
        // reallocate memory for blob array
        numContours++;
        blobs = (Blob *)realloc(blobs, numContours * sizeof(Blob));
        // make and fill blob
        Blob blob;
        CvRect area = cvBoundingRect(contours, 0);
        blob.frarea = area;
        blob.mrarea = cvMinAreaRect2(contours, 0);
        //blob.index = cvPoint(-1, -1);
        
        // allocate memory for blob's mask and fill it with zeros
        blob.mask = cvCreateMat(area.height, area.width, CV_8UC1);
        cvSet(blob.mask, cvScalar(0, 0, 0, 0), NULL);

        // offset point - top left point of the mask
        CvPoint offset = cvPoint(area.x, area.y);
        
        // draw external contour
        fill_single_contour(contours, offset, cvScalar(255, 0, 0, 0),
                                cvScalar(255, 0, 0, 0), blob.mask);
        
        // draw internal contours of holes
        CvSeq *holeContour = contours->v_next;
        while (holeContour != NULL)
        {
            fill_single_contour(holeContour, offset, cvScalar(0, 0, 0, 0),
                                    cvScalar(255, 0, 0, 0), blob.mask);
            holeContour = holeContour->h_next;
        }
        
        // copy blob into the output blob array
        blobs[numContours - 1] = blob;

        // get next element of top level contour hierarchy
        contours = contours->h_next;
    }
    
    // create output blob array
    BlobArray blobArray = {blobs, numContours};

    // release allocated memory
    cvReleaseMat(&clonedImage);
    free(clonedImage);
    cvReleaseMemStorage(&memoryStorage);
    free(memoryStorage);

    return blobArray;
}


/*! \fn draw_blobs(BlobArray blobs, CvMat *canvas)
 * \param [in] blobs - array of blobs to draw.
 * \param [in,out] canvas - pointer to CvMat to draw on.
 *
 * Draws all Blob variables from the BlobArray on the given image canvas.
 * Image canvas should be single channel 8 bit image.
 */ 
void draw_blobs(BlobArray blobs, CvMat *canvas)
{
    for (unsigned i = 0; i < blobs.sz; i++)
    {
        // read info about certain blob
        CvRect area = blobs.blobs[i].frarea; // sub-frame
        CvMat *mask = blobs.blobs[i].mask;// mask
        
        // create header for corresponding area on the canvas
        CvMat subCanvas;// = cvCreateMatHeader(area.height, area.width, CV_8UC1);
        cvGetSubRect(canvas, &subCanvas, area);

        // copy data
        cvCopy(mask, &subCanvas, mask);
        
        //free(subCanvas);
    }
}


/*!\fn fill_single_contour(CvSeq *contour, CvPoint offset, CvScalar interiorColor,
                                    CvScalar contourColor, CvMat *canvas)
*\param [in] contour - input sequence of points.
*\param [in] offset - offset point, which is the top left point of canvas.
*\param [in] interiorColor - color to draw interior with.
*\param [in] contourColor - color to draw contour with.
*\param [in,out] canvas - the canvas to draw on.
*
* It's internal low-level function to make connected components from contours
* shich are found with OpenCV functionality. This function is used when
* find_blobs() function is running.
*/
void fill_single_contour(CvSeq *contour, CvPoint offset, CvScalar interiorColor,
                                    CvScalar contourColor, CvMat *canvas)
{
    // number of points 
    int npts = contour->total;
    // allocate memory for point array
    CvPoint *pointArray = (CvPoint*)malloc(npts * sizeof(CvPoint));
    // number of contours
    int numContours = 1;

    // read points from their sequence to array, subtracting the offset point
    for (int i = 0; i < npts; i++)
    {
        CvPoint p = *CV_GET_SEQ_ELEM(CvPoint, contour, i);
        pointArray[i] = cvPoint(p.x - offset.x, p.y - offset.y);
    }

    // fill area inside polygon of points with given color
    cvFillPoly(canvas, &pointArray, &npts, numContours, interiorColor, 8, 0);

    // draw contour 
    for (int i = 0; i < npts; i++)
    {
        CV_MAT_ELEM(*canvas, uchar, pointArray[i].y, pointArray[i].x) =
                                                         (uchar)contourColor.val[0];
    }
    
    // free allocated memory
    free(pointArray);
}


/*!\fn filter_blobs_with_size(BlobArray *blAr, double minSize, double maxSize)
 * \param [in,out] blAr - array of blobs to filter.
 * \param [in] minSize - minimal possible size for blob to select.
 * \param [in] maxSize - maximal possible size for blob to select.
 *
 * It's the function to filter Blob variables from BlobArray structure by their 
 * geometrical sizes taken from mrarea field. Both the width and the height of
 * mrarea size are compared with minimal and maximal possible Blob sizes taken
 * from function parameters. Even if one of comparements gives doens't satisfy
 * the current Blob is filtered, i.e. it is removed from BlobArray.
 */
void filter_blobs_with_size(BlobArray *blAr, double minSize, double maxSize)
{
    Blob *outBlobs = NULL;
    int outSz = 0;
    for (unsigned i = 0; i < blAr->sz; i++)
    {
        double w = blAr->blobs[i].mrarea.size.width;
        double h = blAr->blobs[i].mrarea.size.height;

        if (w >= minSize && w <= maxSize && h >= minSize && h <= maxSize)
        {
            outSz++;
            outBlobs = (Blob *)realloc(outBlobs, outSz * sizeof(Blob));
            outBlobs[outSz - 1] = blAr->blobs[i];
        }
        else
            release_blob(&blAr->blobs[i]);
    }

    free(blAr->blobs);

    blAr->blobs = outBlobs;
    blAr->sz = outSz;
}


/*! \fn filter_blobs_with_side_ratio(BlobArray *blAr, double maxRatio)
 * \param [in,out] blAr - array of blobs to filter.
 * \param [in] maxRatio - maximal possible side ratio for blob to select.
 *
 * It's the function to filter Blob variables from BlobArray structure by their
 * geometrical proportion taken as fraction of the maximal and the minimal Blob
 * sizes from its mrarea field. This fraction is compared with maximal side 
 * ratio which is given as the function parameter.
 */
void filter_blobs_with_side_ratio(BlobArray *blAr, double maxRatio)
{
    Blob *outBlobs = NULL;
    int outSz = 0;
    for (unsigned i = 0; i < blAr->sz; i++)
    {
        CvSize2D32f sz = blAr->blobs[i].mrarea.size;
        double maxSide = sz.width > sz.height ? sz.width : sz.height;
        double minSide = sz.width < sz.height ? sz.width : sz.height;

        if (maxSide/minSide <= maxRatio)
        {
            outSz++;
            outBlobs = (Blob *)realloc(outBlobs, outSz * sizeof(Blob));
            outBlobs[outSz - 1] = blAr->blobs[i];
        }
        else
            release_blob(&blAr->blobs[i]);
    }

    free(blAr->blobs);

    blAr->blobs = outBlobs;
    blAr->sz = outSz;
}


/*!\fn filter_blobs_with_density(BlobArray *blAr, 
                                    double minDensity, double maxDensity)
*\param [in,out] blAr - array of blobs to filter.
*\param [in] minDensity - minimal possible density for blob to select.
*\param [in] maxDensity - maximal possible density for blob to select.
*
* This function filters Blob variables from BlobArray by the filling density
* of the Blob frarea (i.e. corresponding subframe on the original binary image)
* with pixels which belong to the current Blob. Particular Blob is filtered
* if its filling density is bigger than given maximal density or less than
* given minimal density.
*/
void filter_blobs_with_density(BlobArray *blAr, 
                                    double minDensity, double maxDensity)
{
    Blob *outBlobs = NULL;
    int outSz = 0;
    for (unsigned i = 0; i < blAr->sz; i++)
    {
        double s = blAr->blobs[i].frarea.width * 
                   blAr->blobs[i].frarea.height;

        double density = cvSum(blAr->blobs[i].mask).val[0]/255./s;

        if (density >= minDensity && density <= maxDensity)
        {
            outSz++;
            outBlobs = (Blob *)realloc(outBlobs, outSz * sizeof(Blob));
            outBlobs[outSz - 1] = blAr->blobs[i];
        }
        else
            release_blob(&blAr->blobs[i]);
    }

    free(blAr->blobs);

    blAr->blobs = outBlobs;
    blAr->sz = outSz;
}



/*!\fn filter_blobs_with_geometry_regularity(BlobArray *blAr, double maxRelDev)
 * \param [in,out] blAr - array of blobs to filter.
 * \param [in] maxRelDev - maximal relative deviance in the set of distances 
 *  between blobs.
 *
 * This function filters Blob variables from the given BlobArray with their 
 * geometry regularity. Geometrical centers of blobs are collected and 
 * distances between allk centers are calculated. After that for each Blob
 * the distance to the nearest neighbouring Blob is found. Given the array
 * of distances to the nearest neighbour, it finds the median value of this
 * array and names it as character distance. Then Blob set is broken on 
 * clusters. Clusters are made with assumption that minimal distance between
 * elements of these clusters is bigger than character distance multiplied
 * on the coefficient given as function parameter. All clusters except the
 * cluster with the biggest quantity of elements are filtered.
 */
void filter_blobs_with_geometry_regularity(BlobArray *blAr, double maxRelDev)
{
    Blob *outBlobs = NULL;
    int outSz = 0;
    
    // collect array of blob centers
    CvPoint2D32f *blobCenters = (CvPoint2D32f*)malloc(blAr->sz * sizeof(CvPoint2D32f));
    for (unsigned i = 0; i < blAr->sz; i++)
        blobCenters[i] = blAr->blobs[i].mrarea.center;

    // calculate distances between blobs
    double **distances = calculate_distances(blobCenters, blAr->sz);
    free(blobCenters);

    // calculate distances to nearest neighbours
    double *minDist = (double*)malloc(blAr->sz * sizeof(double));
    for (unsigned i = 0; i < blAr->sz; i++)
    {
        minDist[i] = (i == 0) ? distances[i][1] : distances[i][0];
        for (unsigned j = 0; j < blAr->sz; j++)
            if (j != i && distances[i][j] < minDist[i])
                minDist[i] = distances[i][j];
    }
    
    // calculate character distance as median in the set of nearest distances
    double charDist = fmedian(minDist, blAr->sz);

    // allocate memory for array of cluster labels
    int *labels = (int*)malloc(blAr->sz * sizeof(int));
    for (unsigned i = 0; i < blAr->sz; i++)
        labels[i] = i;

    for (unsigned i = 0; i < blAr->sz; i++)
        for (unsigned j = i+1; j < blAr->sz; j++)
        {
            // if distance between two blobs in near to regular
            if (fabs(distances[i][j] - charDist) <= maxRelDev * charDist 
                    && labels[i] != labels[j])
            {
                // merge their clusters
                for (unsigned k = 0; k < blAr->sz; k++)
                    if (labels[k] == labels[j])
                        labels[k] = labels[i];
            }
        }
    
    // get the biggest cluster number
    int clusterId = modal_value(labels, blAr->sz); 

    for (unsigned i = 0; i < blAr->sz; i++)
    {

        if (labels[i] == clusterId)
        {
            outSz++;
            outBlobs = (Blob *)realloc(outBlobs, outSz * sizeof(Blob));
            outBlobs[outSz - 1] = blAr->blobs[i];
        }
        else
            release_blob(&blAr->blobs[i]);
    }

    // free allocated memory
    for (unsigned i = 0; i < blAr->sz; i++)
        free(distances[i]);
    free(distances);

    free(minDist);
    free(labels);

    // free old blobs
    free(blAr->blobs);

    // rewrite blob array
    blAr->blobs = outBlobs;
    blAr->sz = outSz;
}


/*! \fn filter_blobs_on_grid(BlobArray *blAr, const CvPoint *gridIndexes,
                          int rows, int cols, double criticalNgbAreaRatio)
*\param [in,out] blAr - array of blobs to filter.
*\param [in] gridIndexes - array of 2D indexes for each blob.
*\param [in] rows - number of rows in the partial mark.
*\param [in] cols - number of columns in the partial matrix.
*\param [in] criticalNgbAreaRatio - critical value for areas ratio between two blobs.
*
* This function filters Blob variables from BlobArray having their approximate 
* location on the 2D rectangular grid of blobs. For each blob its geometrical area
* is calculated with the areas of its neighbours. In the calculated dataset of areas
* the median value is found. If the area is quite different from calculated median
* value, the current Blob is filtered. The area difference degree is estimated as
* comparing result of the function parameter with the fraction of the given area and
* median area.
*/ 
void filter_blobs_on_grid(BlobArray *blAr, const CvPoint *gridIndexes,
                          int rows, int cols, double criticalNgbAreaRatio)
{
    // Calculate threshold value for sorting.
    double threshValue = fabs(log(criticalNgbAreaRatio));

    // make 2D matrix of blob pointers
    Blob ***blobMatrix = (Blob***)malloc(rows * sizeof(Blob**));
    for (int i = 0; i < rows; i++)
        blobMatrix[i] = (Blob**)malloc(cols * sizeof(Blob*));

    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
            blobMatrix[i][j] = NULL;
    
    // fill 2D matrix of blob pointers
    for (unsigned i = 0; i < blAr->sz; i++)
        blobMatrix[gridIndexes[i].y][gridIndexes[i].x] = &blAr->blobs[i];
    
    int *flags = (int*)malloc(blAr->sz * sizeof(int));
    for (unsigned i = 0; i < blAr->sz; i++)
        flags[i] = 0;

    
    int **pointIds = allocate_2d_int_matrix(rows, cols, -1);
    for (int i = 0; i < (int)blAr->sz; i++)
    {
        if (pointIds[gridIndexes[i].y][gridIndexes[i].x] != -1)
            flags[i] = 1;
        else
            pointIds[gridIndexes[i].y][gridIndexes[i].x] = i;
    }
    
    // for each blob in 2D matrix
    for (int i = 0; i < rows; i++)
    {
        for (int j = 0; j < cols; j++)
        {
            // if pointer is NULL get next blob
            if (blobMatrix[i][j] == NULL)
                continue;

            // calculate area
            double area = blobMatrix[i][j]->frarea.width*
                          blobMatrix[i][j]->frarea.height;

            // collect non NULL neighbours statistics
            double *ngbData = NULL;
            int ngbDataSize = 0;
            int si = i > 0 ? i - 1 : 0;
            int fi = i < rows - 1 ? i + 1 : rows - 1;
            int sj = j > 0 ? j - 1 : 0;
            int fj = j < cols - 1 ? j + 1 : cols - 1;
            
            // and calculate areas of all neighbours
            for (int k = si; k <= fi; k++)
                for (int l = sj; l <= fj; l++)
                {
                    //if (k == i && l == j)
                    //    continue;
                        
                    if (blobMatrix[k][l] != NULL)
                    {
                        ngbDataSize++;
                        ngbData = (double *)realloc(ngbData, ngbDataSize * sizeof(double));
                        ngbData[ngbDataSize - 1] = blobMatrix[k][l]->frarea.width*
                                                   blobMatrix[k][l]->frarea.height;
                    }
                }
            
            // if area of current blob is distinct from median value
            // then clear current blob
            if (ngbDataSize == 0 || fabs(log(fmedian(ngbData, ngbDataSize)/area)) > threshValue)
            {
                flags[pointIds[i][j]] = 1;
                blobMatrix[i][j] = NULL;
            }

            free(ngbData);
        }
    }
    
    // output blobs structure
    Blob *outBlobs = NULL;
    int outSz = 0;
    
    // release labeled blobs, write others into new array
    for (unsigned i = 0; i < blAr->sz; i++)
    {
        if (flags[i] == 1)
            release_blob(&blAr->blobs[i]);
        else
        {
            outSz++;
            outBlobs = (Blob*)realloc(outBlobs, outSz * sizeof(Blob));
            outBlobs[outSz - 1] = blAr->blobs[i];
        }
    }
    
    // free allocated memory for Blob pointers
    for (int i = 0; i < rows; i++)
        free(blobMatrix[i]);
    free(blobMatrix);

    free_2d_int_matrix(pointIds, rows);
    free(flags);

    // rewrite blob array
    free(blAr->blobs);
    blAr->sz = outSz;
    blAr->blobs = outBlobs;
}


/*!\fn release_blob(Blob *blob)
 * \param [in,out] blob - blob to release.
 */
void release_blob(Blob *blob)
{
    cvReleaseMat(&(blob->mask));
    free(blob->mask);
    blob->mask = NULL;
    //free(blob);
}

/*! \fn release_blob_array(BlobArray *blAr)
 * \param [in,out] blAr - array of blobs to release.
 */
void release_blob_array(BlobArray *blAr)
{
    // release masks for all blobs
    for (unsigned i = 0; i < blAr->sz; i++)
        release_blob(&(blAr->blobs[i]));

    // release array of blobs
    free(blAr->blobs);
    blAr->blobs = NULL;
}


/*!\fn isLeftNeighbour(Blob b1, Blob b2)
 * \param [in] b1 - first blob.
 * \param [in] b2 - second blob.
 * \return degree if second blobs is the left neighbour of first blob.
 *
 * Estimate the degree of Blob b2 is the nearest left neighbour of Blob b1. This
 * degree is calculated as -DBL_MAX if the center of b2 is right than center of b1,
 * otherwise it's calculated as the fraction of vertical overlapping between the blobs
 * and horizontal distance between their centers.
 */
double isLeftNeighbour(Blob b1, Blob b2)
{
    int x1 = (int)b1.mrarea.center.x;
    int x2 = (int)b2.mrarea.center.x;

    if (x2 >= x1)
        //return 0;
        return -DBL_MAX;

    CvRect r1 = b1.frarea;
    CvRect r2 = b2.frarea;

    double ytl = (r1.y > r2.y) ? r1.y : r2.y;
    double ybr = (r1.y + r1.height > r2.y + r2.height) ? 
                r2.y + r2.height : r1.y + r1.height;

    //if (ytl > ybr)
    //    return 0;

    return 1.0*(ybr - ytl)/(x1 - x2 + 1);// +1 for numerical correctness
}


/*!\fn isRightNeighbour(Blob b1, Blob b2)
 * \param [in] b1 - first blob.
 * \param [in] b2 - second blob.
 * \return degree if second blobs is the right neighbour of first blob.
 *
 * Estimate the degree of Blob b2 is the nearest right neighbour of Blob b1. This
 * degree is calculated as equal to the degree that Blob b1 is the nearest left
 * neighbour of Blob b2.
 */
double isRightNeighbour(Blob b1, Blob b2)
{
    return isLeftNeighbour(b2, b1);
}

/*!\fn isTopNeighbour(Blob b1, Blob b2)
 * \param [in] b1 - first blob.
 * \param [in] b2 - second blob.
 * \return degree if second blobs is the top neighbour of first blob.
 *
 * Estimate the degree of Blob b2 is the nearest top neighbour of Blob b1. This
 * degree is calculated as -DBL_MAX if the center of b2 is lower than center of b1,
 * otherwise it's calculated as the fraction of horizontal overlapping between the blobs
 * and vertical distance between their centers.
 */
double isTopNeighbour(Blob b1, Blob b2)
{
    int y1 = (int)b1.mrarea.center.y;
    int y2 = (int)b2.mrarea.center.y;

    if (y2 >= y1)
        //return 0;
        return -DBL_MAX;

    CvRect r1 = b1.frarea;
    CvRect r2 = b2.frarea;

    double xtl = (r1.x > r2.x) ? r1.x : r2.x;
    double xbr = (r1.x + r1.width > r2.x + r2.width) ? 
                r2.x + r2.width : r1.x + r1.width;

    //if (xtl > xbr)
    //    return 0;

	return 1.0*(xbr - xtl)/(y1 - y2 + 1);
}


/*!\fn isDownNeighbour(Blob b1, Blob b2)
 * \param [in] b1 - first blob.
 * \param [in] b2 - second blob.
 * \return degree if second blobs is the down neighbour of first blob.
 *
 * Estimate the degree of Blob b2 is the nearest down neighbour of Blob b1. This
 * degree is calculated as equal to the degree that Blob b1 is the nearest top
 * neighbour of Blob b2.
 */
double isDownNeighbour(Blob b1, Blob b2)
{
    return isTopNeighbour(b2, b1);
}

/*!\fn look_for_connections(double (*funcPtr)(Blob, Blob), 
                         int *ngbIndexes, BlobArray blAr,
                         int searchElemInd, double arrangeThresh)
*\param [in] funcPtr - pointer to Blob connection operation.
*\param [in] ngbIndexes - array of connection indexes.
*\param [in] blAr - array of blobs.
*\param [in] searchElemInd - index of first blob to connect.
*\param [in] arrangeThresh - threshold value for connection.
*\return index of second element to connect.
*
* Finds the nearest neighbour index for the given Blob in the case of
* given Blob doesn't have the particular neighbour. Nearest neighbour
* candidates are looked for in the set of Blob which don't have opposite
* neighbour. The distance to the closest suitable candidate is compared
* with the given threshold value. If there are no closest suitable candidate
* -1 will be returned.
*/
int look_for_connections(double (*funcPtr)(Blob, Blob), 
                         int *ngbIndexes, BlobArray blAr,
                         int searchElemInd, double arrangeThresh)
{
    
    double *degrees = NULL;
    int *inds = NULL;
    int _sz = 0;
    for (int j = searchElemInd + 1; j < (int)blAr.sz; j++)
    {
        if (ngbIndexes[j] == -1)
        {
            _sz++;
            degrees = (double*)realloc(degrees, _sz*sizeof(double));
            degrees[_sz-1] = (*funcPtr)(blAr.blobs[searchElemInd], blAr.blobs[j]);
            inds = (int*)realloc(inds, _sz*sizeof(int));
            inds[_sz - 1] = j;
        }
    }
    
        
    if (_sz == 0)
        return -1;
    
    
    double _max;
    int _maxInd;

    maximum_double_1d(degrees, _sz, &_max, &_maxInd);
    
    int ret = -1;

    if (_max > arrangeThresh)
    {
        ngbIndexes[inds[_maxInd]] = searchElemInd;
        ret = inds[_maxInd];
    }

    if (_sz > 0)
    {
        free(degrees);
        free(inds);
    }
    
    return ret;
}

