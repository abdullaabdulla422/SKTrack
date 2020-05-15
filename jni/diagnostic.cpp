#include "diagnostic.h"
#include "data_processing.h"
#include "math.h"

#ifdef CALC_DIAGNOSTIC_INFO

/*!\fn allocate_empty_diagnostic_info(const RecMark *recMark, _SymbolDiagnostic ****symbols)
\param [in] height - height of mark in data symbols.
\param [in] width - width of mark in data symbols.
\param [out] symbols - output allocated data for found symbols.
\return default found mark structure.
*/
_MarkDiagnostic allocate_empty_diagnostic_info(int height, int width, _SymbolDiagnostic ****symbols)
{
	_MarkDiagnostic mark;
	mark.focus = _point(-1, -1);
	mark.frameLowerRight = _point(-1, -1);
	mark.frameUpperLeft = _point(-1, -1);
	mark.angle = -1;

	*symbols = (_SymbolDiagnostic***)malloc((height + 1) * sizeof(_SymbolDiagnostic**));
	for (int i = 0; i < height + 1; i++)
		(*symbols)[i] = (_SymbolDiagnostic**)malloc((width + 1) * sizeof(_SymbolDiagnostic*));
	for (int i = 0; i < height + 1; i++)
		for (int j = 0; j < width + 1; j++)
			(*symbols)[i][j] = NULL;

	return mark;
}

/*!\fn get_diagnostic_info(bool diagnostics, RecMark recMark, CvPoint cropMove, 
                                        double resizeCoef, AlignInfo alignInfo, 
                                                    SymbolDiagnostic **symbols)
*\param [in] diagnostics - diagnostics flag from context structure.
*\param [in] recMark - input recognition mark info.
*\param [in] cropMove - info for crop compensating.
*\param [in] resizeCoef - info for resizing compensating.
*\param [in] alignInfo  - info for alignment compensating.
*\param [out] symbols - info about recognized symbols.
*\return mark diagnostic info structure.
*/
_MarkDiagnostic get_diagnostic_info(bool diagnostics, RecMark recMark, CvPoint cropMove, 
                                        double resizeCoef, AlignInfo alignInfo, 
                                                    _SymbolDiagnostic ****symbols)
{
    int sz = recMark.symbols.sz;
	
    // allocate memory for output SymbolDiagnostic array
    //*symbols = (_SymbolDiagnostic*)malloc(sz * sizeof(_SymbolDiagnostic));
	_MarkDiagnostic outMarkD = allocate_empty_diagnostic_info(recMark.height, recMark.width, symbols);
	outMarkD.angle = (alignInfo.alignAngle != -1 && recMark.angle != -1) ?
                            (360 - alignInfo.alignAngle + recMark.angle)%360 : -1;

	if (!diagnostics)
		return outMarkD;

	CvPoint *localUpperLeft = (CvPoint*)malloc(sz * sizeof(CvPoint));
    CvPoint *localLowerRight = (CvPoint*)malloc(sz * sizeof(CvPoint));
     

	// initialize an array of SymbolDiagnostic
	/*
    for (int i = 0; i < sz; i++)
    {
        (*symbols)[i].isDataSymbol = recMark.types[i];
        localUpperLeft[i] = recMark.upperLeft[i];
        localLowerRight[i] = recMark.lowerRight[i];
        (*symbols)[i].value = (unsigned char)recMark.values[i];

    }
	*/

	if (recMark.error == 0)
		for (int i = 0; i < recMark.height + 1; i++)
			for (int j = 0; j < recMark.width + 1; j++)
			{
				int ind = recMark.indexes[i][j];

				if (ind >= 0)
				{
					(*symbols)[i][j] = (_SymbolDiagnostic *)malloc(sizeof(_SymbolDiagnostic));
					(*symbols)[i][j]->isDataSymbol = recMark.types[ind];
					localUpperLeft[ind] = recMark.upperLeft[ind];
					localLowerRight[ind] = recMark.lowerRight[ind];
					(*symbols)[i][j]->value = (unsigned char)recMark.values[ind];
				}
			}

    // calculate inverse affine transform
    CvMat *mapMatrix = alignInfo.mapMatrix;
    CvPoint2D32f shift = cvPoint2D32f((-1)*CV_MAT_ELEM(*mapMatrix, float, 0, 2),
                            (-1)*CV_MAT_ELEM(*mapMatrix, float, 1, 2));

    CvMat *rotMatrix = cvCreateMat(2, 2, CV_32FC1);
    CV_MAT_ELEM(*rotMatrix, float, 0, 0) = CV_MAT_ELEM(*mapMatrix, float, 0, 0);
    CV_MAT_ELEM(*rotMatrix, float, 0, 1) = (-1)*CV_MAT_ELEM(*mapMatrix, float, 0, 1);
    CV_MAT_ELEM(*rotMatrix, float, 1, 0) = (-1)*CV_MAT_ELEM(*mapMatrix, float, 1, 0);
    CV_MAT_ELEM(*rotMatrix, float, 1, 1) = CV_MAT_ELEM(*mapMatrix, float, 1, 1);

    // apply inverse transform for SymbolDiagnostic objects.
    for (int i = 0; i < sz; i++)
    {
        CvPoint p1 = localUpperLeft[i];
        CvPoint p2 = localLowerRight[i];
        
		choose_diagnostic_points(&p1, &p2, recMark.angle);
		/*
        transform_point_pair(p1, p2, shift, rotMatrix, 
                            &(localUpperLeft[i]), 
                            &(localLowerRight[i]));
							*/
		localUpperLeft[i] = transform_point(p1, shift, rotMatrix);
		localLowerRight[i] = transform_point(p2, shift, rotMatrix);
    }
      
	// apply inverse transform for MarkDiagnostic object.
    CvPoint outFocus, outFrameUpperLeft, outFrameLowerRight;
    outFocus = transform_point(recMark.focus, shift, rotMatrix);
	/*
    transform_point_pair(cvPoint(0, 0), 
                         cvPoint(alignInfo.alignedImage->cols - 1,
                                 alignInfo.alignedImage->rows - 1),
                         shift, rotMatrix, 
                         &outFrameUpperLeft, &outFrameLowerRight);
						 */
	outFrameUpperLeft = cvPoint(0, 0);
	outFrameLowerRight = cvPoint(alignInfo.alignedImage->cols - 1, alignInfo.alignedImage->rows - 1);
	choose_diagnostic_points(&outFrameUpperLeft, &outFrameLowerRight, recMark.angle);
	outFrameUpperLeft = transform_point(outFrameUpperLeft, shift, rotMatrix);
	outFrameLowerRight = transform_point(outFrameLowerRight, shift, rotMatrix);

    cvReleaseMat(&rotMatrix);
    
    // restore resize and crop modifications
    
    // for MarkDiagnostic object
    outFocus = resize_crop_restore(outFocus, resizeCoef, cropMove);
    outFrameUpperLeft = resize_crop_restore(outFrameUpperLeft,
                                            resizeCoef, cropMove);
    outFrameLowerRight = resize_crop_restore(outFrameLowerRight,
                                            resizeCoef, cropMove);

    // for SymbolDiagnostic objects
    for (int i = 0; i < sz; i++)
    {
        localUpperLeft[i] = resize_crop_restore(localUpperLeft[i],
                                                    resizeCoef, cropMove);
        localLowerRight[i] = resize_crop_restore(localLowerRight[i],
                                                    resizeCoef, cropMove);
    }

    // copy data from CvPoint structures to _Point structures
    outMarkD.focus = _point(outFocus.x, outFocus.y);
    outMarkD.frameUpperLeft = _point(outFrameUpperLeft.x, outFrameUpperLeft.y);
    outMarkD.frameLowerRight = _point(outFrameLowerRight.x, outFrameLowerRight.y);

	/*
    for (int i = 0; i < sz; i++)
    {
        (*symbols)[i].frameUpperLeft = _point(localUpperLeft[i].x,
                                              localUpperLeft[i].y);
        (*symbols)[i].frameLowerRight = _point(localLowerRight[i].x,
                                               localLowerRight[i].y);
    }
	*/
	if (recMark.error == 0)
		for (int i = 0; i < recMark.height + 1; i++)
			for (int j = 0; j < recMark.width + 1; j++)
			{
				int ind = recMark.indexes[i][j];

				if (ind >= 0)
				{
					(*symbols)[i][j]->frameUpperLeft = _point(localUpperLeft[ind].x,
                                              localUpperLeft[ind].y);
					(*symbols)[i][j]->frameLowerRight = _point(localLowerRight[ind].x,
                                               localLowerRight[ind].y);
				}
			}

    free(localUpperLeft);
    free(localLowerRight);

    return outMarkD;
}


/*!\fn choose_diagnostic_points(CvPoint *topLeft, CvPoint *downRight, int angle)
* \param [in,out] topLeft - upper left diagnostic point to update.
* \param [in,out] downRight - lower right diagnostic point to update.
* \param [in] angle - angle to take into account.
*/
void choose_diagnostic_points(CvPoint *topLeft, CvPoint *downRight, int angle)
{
	int xtl = topLeft->x;
	int ytl = topLeft->y;
	int xbr = downRight->x;
	int ybr = downRight->y;

	/* 
	* If angle is not in the set {0,90,180,270}, than nothing to do.
	*/

	//printf("Angle is %d\n", angle);
	switch(angle)
	{
		case 180:
			topLeft->x = xbr;
			topLeft->y = ytl;
			downRight->x = xtl;
			downRight->y = ybr;
			break;
		case 270:
			topLeft->x = xbr;
			topLeft->y = ybr;
			downRight->x = xtl;
			downRight->y = ytl;
			break;
		case 0:
			topLeft->x = xtl;
			topLeft->y = ybr;
			downRight->x = xbr;
			downRight->y = ytl;
			break;
		case 90:
			break;
	}
}


/*!\fn transform_point(CvPoint in, CvPoint2D32f shift, CvMat *rotMatrix)
 * \param [in] in - point to transform.
 * \param [in] shift - shift parameter.
 * \param [in] rotMatrix - rotate parameter.
 * \return transformed point.
 */
CvPoint transform_point(CvPoint in, CvPoint2D32f shift, CvMat *rotMatrix)
{
    CvMat *shiftedPoint = cvCreateMat(2, 1, CV_32FC1);

    CV_MAT_ELEM(*shiftedPoint, float, 0, 0) = in.x + shift.x;
    CV_MAT_ELEM(*shiftedPoint, float, 1, 0) = in.y + shift.y;

    CvMat *resultPoint = cvCreateMat(2, 1, CV_32FC1);

    cvGEMM(rotMatrix, shiftedPoint, 1, NULL, 1, resultPoint, 0);

    CvPoint out = cvPoint((int)CV_MAT_ELEM(*resultPoint, float, 0, 0),
                          (int)CV_MAT_ELEM(*resultPoint, float, 1, 0));

    cvReleaseMat(&shiftedPoint);
    cvReleaseMat(&resultPoint);

    return out;
}


/*!\fn transform_point_pair(CvPoint p1In, CvPoint p2In,
                          CvPoint2D32f shift, CvMat *rotMatrix,
                          CvPoint *p1Out, CvPoint *p2Out)
*\param [in] p1In - first input point.
*\param [in] p2In - second input point.
*\param [in] shift - shift parameter.
*\param [in] rotMatrix - rotate parameter.
*\param [out] p1Out - first output point.
*\param [out] p2Out - second output point.
*/
void transform_point_pair(CvPoint p1In, CvPoint p2In,
                          CvPoint2D32f shift, CvMat *rotMatrix,
                          CvPoint *p1Out, CvPoint *p2Out)
{

    CvPoint p1 = transform_point(p1In, shift, rotMatrix);
    CvPoint p2 = transform_point(p2In, shift, rotMatrix);
    CvPoint p3 = transform_point(cvPoint(p1In.x, p2In.y), shift, rotMatrix);
    CvPoint p4 = transform_point(cvPoint(p2In.x, p1In.y), shift, rotMatrix); 

    int x[4] = {p1.x, p2.x, p3.x, p4.x};
    int y[4] = {p1.y, p2.y, p3.y, p4.y};

    int xs[4], ys[4];

    sort1d(x, 4, xs, NULL, false);
    sort1d(y, 4, ys, NULL, false);
    
    *p1Out = cvPoint(xs[0], ys[0]);
    *p2Out = cvPoint(xs[3], ys[3]);
}


/*!\fn resize_crop_restore(CvPoint in, double resizeCoef, CvPoint cropMove)
 *\param [in] in - point for resize and crop compensating.
 *\param [in] resizeCoef - resizing coefficient.
 *\param [in] cropMove - cropping info.
 *\return transformed point.
 */
CvPoint resize_crop_restore(CvPoint in, double resizeCoef, CvPoint cropMove)
{
    CvPoint out;

    out.x = (int)(1.0*in.x*sqrt(resizeCoef) + cropMove.x);
    out.y = (int)(1.0*in.y*sqrt(resizeCoef) + cropMove.y);

    return out;
}


/*! \fn draw_diagnostic_rectangle(CvMat *canvas, CvScalar color, CvPoint upperLeft, CvPoint lowerRight, int angle)
 * \param [in,out] canvas - canvas to to draw on.
 * \param [in] color - color to draw with.
 * \param [in] upperLeft - upper left point of diagnostic rectangle.
 * \param [in] lowerRight - lower right point of diagnostic rectangle.
 * \param [in] angle - orientation of diagnostic rectangle.
 */
void draw_diagnostic_rectangle(CvMat *canvas, CvScalar color, CvPoint upperLeft, CvPoint lowerRight, int angle)
{
	// go to the right coordinate system
	upperLeft.y = -upperLeft.y;
	lowerRight.y = -lowerRight.y;

	float sinA = sin((float)(angle*3.14159/180));
	float cosA = cos((float)(angle*3.14159/180));

	// get line equations
	Line direction = {(float)lowerRight.x, (float)lowerRight.y, cosA, -sinA};
	Line perpendicular = {(float)upperLeft.x, (float)upperLeft.y, -sinA, -cosA};
	CvPoint2D32f *pLowerLeft = get_lines_intersection(direction, perpendicular, (float)0.01);
	CvPoint lowerLeft = cvPoint((int)pLowerLeft->x, (int)pLowerLeft->y);
	free(pLowerLeft);
	CvPoint upperRight = cvPoint(upperLeft.x + lowerRight.x - lowerLeft.x,
								 upperLeft.y + lowerRight.y - lowerLeft.y);

	// return to the left coordinate system
	upperLeft.y = -upperLeft.y;
	lowerRight.y = -lowerRight.y;
	upperRight.y = -upperRight.y;
	lowerLeft.y = -lowerLeft.y;

	cvLine(canvas, upperLeft, lowerLeft, color, 1, 8, 0);
	cvLine(canvas, lowerLeft, lowerRight, color, 1, 8, 0);
	cvLine(canvas, lowerRight, upperRight, color, 1, 8, 0);
	cvLine(canvas, upperRight, upperLeft, color, 1, 8, 0);

	cvCircle(canvas, upperLeft, 4, color, 1, 8, 0);
}

/*! \fn show_diagnostic_info(CvMat *canvas, void *cxt)
 * \param [in] canvas - canvas to draw on.
 * \param [in] cxt - context structure to extract info from.
 */ 
void show_diagnostic_info(CvMat *canvas, void *cxt)
{
	Context *context = (Context *)cxt;

	if (context->diagnostics)
    {
        //CvMat *canvas = cvCloneMat(image);
        
        CvPoint p1 = cvPoint(context->foundMark.frameUpperLeft.x,
                             context->foundMark.frameUpperLeft.y);

        CvPoint p2 = cvPoint(context->foundMark.frameLowerRight.x,
                             context->foundMark.frameLowerRight.y);
        CvPoint focus = cvPoint(context->foundMark.focus.x,
                                context->foundMark.focus.y);

        ////cvRectangle(canvas, p1, p2, CV_RGB(255, 0, 0), 1, 8, 0);
		//cvCircle(canvas, p1, 15, CV_RGB(255, 0, 0), 1, 8, 0);
		//cvRectangle(canvas, cvPoint(p2.x - 15, p2.y - 15), cvPoint(p2.x + 15, p2.y + 15), CV_RGB(255, 0, 0), 1, 8, 0);
		draw_diagnostic_rectangle(canvas, CV_RGB(255, 0, 0), p1, p2, context->foundMark.angle);
        cvCircle(canvas, focus, 5, CV_RGB(255, 0, 0), 1, 8, 0);
        
		/*
        for (int i = 0; i < context->foundSymbolsNumber; i++)
        {
            CvPoint p1 = cvPoint(context->foundSymbols[i].frameUpperLeft.x,
                                 context->foundSymbols[i].frameUpperLeft.y);

            CvPoint p2 = cvPoint(context->foundSymbols[i].frameLowerRight.x,
                                 context->foundSymbols[i].frameLowerRight.y);
            bool isDataSymbol = context->foundSymbols[i].isDataSymbol;
            
            if (isDataSymbol)
                cvRectangle(canvas, p1, p2, CV_RGB(0,255,0),1,8,0);
            else
                cvRectangle(canvas, p1, p2, CV_RGB(0,0,255),1,8,0); 
        }
		*/

		for (int i = 0; i < context->height + 1; i++)
			for (int j = 0; j < context->width + 1; j++)
			{
				_SymbolDiagnostic *symbolDiagn = context->foundSymbols[i][j];
				if (symbolDiagn != NULL)
				{
					CvPoint p1 = cvPoint(symbolDiagn->frameUpperLeft.x, symbolDiagn->frameUpperLeft.y);
					CvPoint p2 = cvPoint(symbolDiagn->frameLowerRight.x, symbolDiagn->frameLowerRight.y);
					bool isDataSymbol = symbolDiagn->isDataSymbol;

					if (isDataSymbol)
					{
						draw_diagnostic_rectangle(canvas, CV_RGB(0, 255, 0), p1, p2, context->foundMark.angle);
						//cvCircle(canvas, p1, 3, CV_RGB(0, 255, 0), 1, 8, 0);
						//cvRectangle(canvas, cvPoint(p2.x - 2, p2.y - 2), cvPoint(p2.x + 2, p2.y + 2), CV_RGB(0, 255, 0), 1, 8, 0);
						////cvRectangle(canvas, p1, p2, CV_RGB(0,255,0),1,8,0);
					}
					else
					{
						draw_diagnostic_rectangle(canvas, CV_RGB(0, 0, 255), p1, p2, context->foundMark.angle);
						//cvCircle(canvas, p1, 3, CV_RGB(0, 0, 255), 1, 8, 0);
						//cvRectangle(canvas, cvPoint(p2.x - 2, p2.y - 2), cvPoint(p2.x + 2, p2.y + 2), CV_RGB(0, 0, 255), 1, 8, 0);
						////cvRectangle(canvas, p1, p2, CV_RGB(0,0,255),1,8,0);
					}
				}
			}

        cvShowImage("Diagnostic image", canvas);
        cvWaitKey(0);
		//cvReleaseMat(&canvas);
    }
}

#endif
