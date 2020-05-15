#include "encoded_mark_translator.h"
//#include <string>
//#include <iostream>
//#include <sstream>
//#include <android/log.h>
//
//
//static int tmp = 0;


/*!\fn mark_finder_init(int width, int height, const char *symbolName)
 * \param [in] width - width of mark in symbols.
 * \param [in] height - height of mark in symbols.
 * \param [in] symbolName - the name of recognized symbol.
 * \return pointer to initialized context structure.
 */
void *mark_finder_init(int width, int height)
/*
#ifdef WIN32
	#ifdef DLLBUILD
extern "C"
{
__declspec(dllexport) void *MarkFinder_Init(int width, int height)
	#else
void *MarkFinder_Init(int width, int height)
	#endif
#else
void *MarkFinder_Init(int width, int height)
#endif
*/
{
	char defaultFocusName[80] = "Symbol";
    // after we added 'V' symbol this line is deprecated
	char defaultSymbolName[80] = "Y";


    Context *context = (Context*)malloc(sizeof(Context));
    context->lightOnDark = false;
    context->diagnostics = false; 
    context->flipped = false;

    strcpy(context->focusName, defaultFocusName);
    strcpy(context->symbolName , defaultSymbolName);
    //strcpy(context->symbolName, symbolName);
    
    context->width = width;
    context->height = height;
    context->missing = 0;
    
    context->regionUpperLeft = _point(-1, -1);
    context->regionLowerRight = _point(-1, -1);

#ifdef CALC_DIAGNOSTIC_INFO
    context->foundMark.focus = _point(-1, -1);
    context->foundMark.frameUpperLeft = _point(-1, -1);
    context->foundMark.frameLowerRight = _point(-1, -1);
    context->foundMark.angle = -1;

    context->foundSymbols = NULL;
#endif
    context->foundSymbolsNumber = 0;

    context->bitmapInfo.height = 0;
    context->bitmapInfo.width = 0;
    context->bitmapInfo.stride = 0;
    context->bitmapInfo.format = 0;

    context->optimalBitmap = _size(640, 480);

    context->algSettings.segmBlockSize = 21;
    context->algSettings.segmShiftValue = -22.;
    context->algSettings.minBlobSize = 5.;
    context->algSettings.maxBlobSize = 60.;
    context->algSettings.maxBlobSideRatio = 2.;
    context->algSettings.minBlobDensity = 0.1;
    context->algSettings.maxBlobDensity = 0.9;
    context->algSettings.maxGeomRegParam = 0.47;
    context->algSettings.criticalNgbAreaRatio = 1.5;
    context->algSettings.badSymbolRecThresh = 0.05;
	context->algSettings.findFocusNumThresh = 5;
    context->algSettings.arrangeThresh = -0.1;
    context->algSettings.areOptionsSet = true;

    context->resultCode = NO_RESULT;

    return (void *)context;
}

/*
#ifdef WIN32
#ifdef DLLBUILD
}
#endif
#endif
*/

/*!\fn mark_finder_free(void *context)
 * \param [in] context - context structure to free.
 */
void mark_finder_free(void *context)
/*
#ifdef WIN32
	#ifdef DLLBUILD
extern "C"
{
 __declspec(dllexport) void MarkFinder_Free(void *context)
	#else
void MarkFinder_Free(void *context)
	#endif
#else
void MarkFinder_Free(void *context)
#endif
*/
{
    Context *cxt = (Context *)context;

#ifdef CALC_DIAGNOSTIC_INFO
	for (int i = 0; i < cxt->height + 1; i++)
		for (int j = 0; j < cxt->width + 1; j++)
			if (cxt->foundSymbols[i][j] != NULL)
				free(cxt->foundSymbols[i][j]);
	
	for (int i = 0; i < cxt->height + 1; i++)
		free(cxt->foundSymbols[i]);

    free(cxt->foundSymbols);
#endif

    free(cxt);
}

/*
#ifdef WIN32
#ifdef DLLBUILD
}
#endif
#endif
*/

/*! \fn mark_finder_translate(void *context, unsigned char *bitmap)
 * \param [in] context - context for translating.
 * \param [in] bitmap - input OpenCV gray image.
 * \param [in] enhance - do image enhancement or not.
 * \return result of translating.
 *
 * Flip image if it needs.
 * Then crop subframe which corresponds to the set ROI.
 * After that resize image if it's too large, convert to gray-scale and invert it if it needs.
 * When preprocessing is finished binarize an image, align it and find blobs on the alignment image.
 * Found blobs should be translated to the mark. If it needs calculate additional diagnostic info.
 * Last stage is to build output array.
 */
void *mark_finder_translate(void *context, CvMat *bitmap, bool enhance)
/*
#ifdef WIN32
	#ifdef DLLBUILD
extern "C"
{
 __declspec(dllexport) void *MarkFinder_Translate(void *context, unsigned char *bitmap)
	#else
void *MarkFinder_Translate(void *context, unsigned char *bitmap)
	#endif
#else
void *MarkFinder_Translate(void *context, unsigned char *bitmap)
#endif
*/
{
    // get context
    Context *cxt = (Context *)context;

	cxt->missing = 0;
	cxt->foundSymbolsNumber = 0;

    // get image
    //CvMat mat;
    //CvMat *image = (get_cvmat_from_raw_data(&mat, cxt->bitmapInfo.height, 
    //            cxt->bitmapInfo.width, cxt->bitmapInfo.format, bitmap) == 0) ? 
    //            cvCloneMat(&mat) : NULL;


    if (bitmap == NULL)
    {
        cxt->resultCode = NO_IMAGE;
        strcpy(cxt->resultMessage, "unable to get image");
#ifdef CALC_DIAGNOSTIC_INFO
		cxt->foundMark = allocate_empty_diagnostic_info(cxt->height, cxt->width, &cxt->foundSymbols);
#endif
        return NULL;
    }

#if defined CALC_DIAGNOSTIC_INFO && defined SHOW_FINAL_DIAGNOSTIC
	CvMat *diagnosticCanvas = cvCloneMat(bitmap);
#endif

    // get region of interest from the image
    CvPoint tl = cvPoint(cxt->regionUpperLeft.x,
                         cxt->regionUpperLeft.y);

    CvPoint br = cvPoint(cxt->regionLowerRight.x + 1,
                         cxt->regionLowerRight.y + 1);

    if (tl.x < 0 || tl.y < 0 || br.x > bitmap->cols || br.y > bitmap->rows)
    {
#ifdef FULL_CV_DEBUG
		printf("TL = (%d, %d) BR = (%d, %d)\n", tl.x, tl.y, br.x, br.y);
		printf("Image size is %dx%d\n", image->rows, image->cols);
#endif
        cxt->resultCode = BAD_PARAMETER;
        strcpy(cxt->resultMessage, "incorrect region of interest");
#ifdef CALC_DIAGNOSTIC_INFO
		cxt->foundMark = allocate_empty_diagnostic_info(cxt->height, cxt->width, &cxt->foundSymbols);
#endif
#if defined CALC_DIAGNOSTIC_INFO && defined SHOW_FINAL_DIAGNOSTIC
	    cvReleaseMat(&diagnosticCanvas);
#endif

        return NULL;
    }

// crop image
    CvMat subImage;
    cvGetSubRect(bitmap, &subImage, cvRect(tl.x, tl.y, br.x - tl.x, br.y - tl.y));
    CvMat *croppedImage = &subImage;

//    std::stringstream filename;
//    filename << "/sdcard/sk/" << tmp++ << ".jpg";
//    __android_log_write(ANDROID_LOG_INFO, "SKScanner", filename.str().c_str());
//    cvSaveImage(filename.str().c_str(), croppedImage);

    CvPoint cropMovement = cvPoint(tl.x, tl.y);

#ifdef FULL_CV_DEBUG
	printf("crop point is (%d,%d)\n", cropMovement.x, cropMovement.y);
	cvShowImage("Cropped image", croppedImage);
	cvWaitKey(0);
#endif

    // resize image if it needed
    if (cxt->optimalBitmap.width < 1 || cxt->optimalBitmap.height < 1)
    {
        cxt->resultCode = BAD_PARAMETER;
        strcpy(cxt->resultMessage, "incorrect optimal size of the bitmap");
#ifdef CALC_DIAGNOSTIC_INFO
		cxt->foundMark = allocate_empty_diagnostic_info(cxt->height, cxt->width, &cxt->foundSymbols);
#endif
#if defined CALC_DIAGNOSTIC_INFO && defined SHOW_FINAL_DIAGNOSTIC
	    cvReleaseMat(&diagnosticCanvas);
#endif
        return NULL;
    }
    double coef = 1.0 * (br.y - tl.y)*(br.x - tl.x) / 
                   (cxt->optimalBitmap.width*cxt->optimalBitmap.height);

#ifdef FULL_CV_DEBUG
	printf("resizing coef = %f\n", coef);
#endif

    coef = coef > 1 ? coef : 1;
    CvMat *grayImage = NULL;

    if (coef > 1)
    {
        int newWidth = (int)((br.x - tl.x)/sqrt(coef));
        int newHeight = (int)((br.y - tl.y)/sqrt(coef));
        grayImage = cvCreateMat(newHeight, newWidth, croppedImage->type);
        cvResize(croppedImage, grayImage, CV_INTER_LINEAR);
    }
    else
        grayImage = cvCloneMat(croppedImage);

	// flip image if it is needed
    if (cxt->flipped)
        cvFlip(grayImage, NULL, 0);

    if (enhance)
    {
        // Laplace (will also invert image)
        CvMat* grayImageLaplaceFloat = cvCreateMat(grayImage->rows, grayImage->cols, CV_32FC1);
        cvLaplace(grayImage, grayImageLaplaceFloat, 5);
        cvConvert(grayImageLaplaceFloat, grayImage);
        cvReleaseMat(&grayImageLaplaceFloat);
    }

    // invert image if it is needed
    if (enhance == cxt->lightOnDark)  // invert only if both true or false
    {
        CvMat *invertedImage = invert_image(grayImage);
        cvCopy(invertedImage, grayImage, NULL);
        cvReleaseMat(&invertedImage);
    }

    // get algorithm settings
    AlgSettings alg = cxt->algSettings;

    if (!alg.areOptionsSet)
    {
        cxt->resultCode = BAD_PARAMETER;
        strcpy(cxt->resultMessage, "algorithm options are not set");
        cvReleaseMat(&grayImage);

#ifdef CALC_DIAGNOSTIC_INFO
		cxt->foundMark = allocate_empty_diagnostic_info(cxt->height, cxt->width, &cxt->foundSymbols);
#endif
#if defined CALC_DIAGNOSTIC_INFO && defined SHOW_FINAL_DIAGNOSTIC
        show_diagnostic_info(diagnosticCanvas, context);
	    cvReleaseMat(&diagnosticCanvas);
#endif
        return NULL;
    }

    // segment mark from the gray scale image
#ifdef FULL_CV_DEBUG 
    cvShowImage("Grayscale, possibly inverted image", grayImage);
    cvWaitKey(0);
#endif

    CvMat *segmMarkImg = binarize_image(grayImage, alg.segmBlockSize,
                        alg.segmShiftValue, alg.minBlobSize, 
                        alg.maxBlobSize, alg.maxBlobSideRatio,
                        alg.minBlobDensity, alg.maxBlobDensity,
                        alg.maxGeomRegParam);
    
    cvReleaseMat(&grayImage);
    int whitePixNum = (int)cvSum(segmMarkImg).val[0]/255;
#ifdef FULL_CV_DEBUG
	printf("Number of white pixels is %d\n",whitePixNum);
#endif
    if (whitePixNum == 0)
    {
        cvReleaseMat(&segmMarkImg);
        free(segmMarkImg);
        cxt->resultCode = NO_SYMBOLS;
        strcpy(cxt->resultMessage, "symbols were not found");
		
#if defined CALC_DIAGNOSTIC_INFO
		cxt->foundMark = allocate_empty_diagnostic_info(cxt->height, cxt->width, &cxt->foundSymbols);
	#if defined SHOW_FINAL_DIAGNOSTIC
        show_diagnostic_info(diagnosticCanvas, context);
	    cvReleaseMat(&diagnosticCanvas);
	#endif
#endif
        return NULL;
    }

#ifdef FULL_CV_DEBUG
    cvShowImage("Segmented image", segmMarkImg);
    cvWaitKey(0);
#endif
    
    // align segmented image
    AlignInfo alignInfo = align_binary_image(segmMarkImg);

    cvReleaseMat(&segmMarkImg);

#ifdef FULL_CV_DEBUG
    cvShowImage("Aligned mark image", alignInfo.alignedImage);
    cvWaitKey(0);
#endif
     
    // find symbols
    BlobArray blobs = find_blobs(alignInfo.alignedImage);
    
    // recognize symbols
    RecMark recMark = translate_blobs_to_mark(blobs, cxt);

    cxt->foundSymbolsNumber = recMark.symbols.sz;

#ifdef CALC_DIAGNOSTIC_INFO
	cxt->foundMark = get_diagnostic_info(cxt->diagnostics, recMark, cropMovement, coef,
                                             alignInfo, &cxt->foundSymbols);
#endif

#ifdef FULL_CV_DEBUG
	printf("recMark error is %d\n", recMark.error);
#endif

    switch(recMark.error)
    {
        case 0:
#ifdef FULL_CV_DEBUG
			printf("Mark sizes are %d x %d\n", cxt->width, cxt->height);
#endif
            cxt->resultCode = SUCCESS;
            strcpy(cxt->resultMessage, "successful mark recognition");
            //calculate number of missings
            for (int i = 0; i < cxt->height; i++)
                for (int j = 0; j < cxt->width; j++)
                    if (recMark.result[i][j] == -1)
                        (cxt->missing)++;
#ifdef FULL_CV_DEBUG
			printf("Number of missings is %d\n", cxt->missing);
#endif
            break;
        case 1:
            cxt->resultCode = BAD_PARAMETER;
            strcpy(cxt->resultMessage, "incorrect input values for mark width and height");
            break;
        case 2:
            cxt->resultCode = NO_FOCUS;
            strcpy(cxt->resultMessage, "focus not found");
            break;
        case 3:
            cxt->resultCode = NO_RESULT;
            strcpy(cxt->resultMessage, "recognized mark does not satisfy to format");
            break;
        default:
            break;
    }
   
#ifdef FULL_CV_DEBUG
	printf("To release: number of rows is %d\n", recMark.height);
#endif
    // free memory allocated during binary image alignment operation
    cvReleaseMat(&alignInfo.alignedImage);
#ifdef FULL_CV_DEBUG
	printf("Here alignInfo.mapMatrix is %p\n", alignInfo.mapMatrix);
#endif 
#ifdef CALC_DIAGNOSTIC_INFO
    #ifdef FULL_CV_DEBUG
    printf("Estimated angle of rotation is %d.\n", cxt->foundMark.angle);
    #endif
#endif
    cvReleaseMat(&alignInfo.mapMatrix);
 
    // free memory allocated during blob recognizing
    free_2d_int_matrix(recMark.indexes, recMark.height + 1);
#ifdef FULL_CV_DEBUG
	printf("Data is free\n");
#endif

#ifdef CALC_DIAGNOSTIC_INFO
    if (recMark.values != NULL)
        free(recMark.values);
    if (recMark.types != NULL)
        free(recMark.types);
    if (recMark.upperLeft != NULL)
        free(recMark.upperLeft);
    if (recMark.lowerRight != NULL)
        free(recMark.lowerRight);
#endif
#ifdef FULL_CV_DEBUG
	printf("Diagnostic info is free\n");
#endif
    release_blob_array(&recMark.symbols);
    
    if (recMark.result == NULL)
    {
#if defined CALC_DIAGNOSTIC_INFO && defined SHOW_FINAL_DIAGNOSTIC
        show_diagnostic_info(diagnosticCanvas, context);
        cvReleaseMat(&diagnosticCanvas);
#endif
        return NULL;
    }


    // write data into the output 1D array
    unsigned char *out = (unsigned char*)
                    malloc(cxt->width*cxt->height*sizeof(unsigned char));

    for (int i = 0; i < cxt->height; i++)
        for (int j = 0; j < cxt->width; j++)
            out[i*cxt->width + j] = (unsigned char)recMark.result[i][j];
    
#ifdef FULL_CV_DEBUG
	printf("Result data is written into output array\n");
#endif
#ifdef FULL_CV_DEBUG
	printf("Recmark height is %d\n", recMark.height);
#endif 
    free_2d_int_matrix(recMark.result, recMark.height);
     
#if defined CALC_DIAGNOSTIC_INFO && defined SHOW_FINAL_DIAGNOSTIC
	show_diagnostic_info(diagnosticCanvas, context);
	cvReleaseMat(&diagnosticCanvas);
#endif

#ifdef FULL_CV_DEBUG
	printf("Before return\n");
#endif 
    return (void*)out;
}

/*
#ifdef WIN32
#ifdef DLLBUILD
}// extern "C"
#endif
#endif
*/

#ifndef DLLBUILD
/*!\fn fill_mark_finder(void *context, CvMat *bitmap, bool diagn, bool lightOnDark)
 * \param [out] context - context structure to fill.
 * \param [in] bitmap - bitmap to translate.
 * \param [in] diagn - true if it's need to calculate diagnostic info,
 *  false otherwise.
 *  \param [in] lightOnDark - true if it's need to recognize light symbols on
 *  dark background, false otherwise.
 */
void fill_mark_finder(void *context, CvMat *bitmap, bool diagn, bool lightOnDark)
{
    Context *cxt = (Context *)context;

	/*
    cxt->algSettings.segmBlockSize = 21;
    cxt->algSettings.segmShiftValue = -22.;
    cxt->algSettings.minBlobSize = 5.;
    cxt->algSettings.maxBlobSize = 60.;
    cxt->algSettings.maxBlobSideRatio = 2.;
    cxt->algSettings.minBlobDensity = 0.1;
    cxt->algSettings.maxBlobDensity = 0.9;
    cxt->algSettings.maxGeomRegParam = 0.47;
    cxt->algSettings.criticalNgbAreaRatio = 1.5;
    cxt->algSettings.badSymbolRecThresh = 0.05;
    cxt->algSettings.arrangeThresh = -0.1;
    cxt->algSettings.areOptionsSet = true;
	*/

    cxt->regionUpperLeft = _point(0, 0);
    cxt->regionLowerRight = _point(bitmap->cols-1, bitmap->rows-1);

    //cxt->optimalBitmap = _size(640, 480);

    cxt->diagnostics = diagn;
    cxt->lightOnDark = lightOnDark;
    cxt->flipped = false;

    cxt->bitmapInfo.height = bitmap->rows;
    cxt->bitmapInfo.width = bitmap->cols;
    cxt->bitmapInfo.format = 0;
    cxt->bitmapInfo.stride = 3 * bitmap->cols;
}

#endif
