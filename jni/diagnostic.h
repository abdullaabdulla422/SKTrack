/*!\file diagnostic.h
 *\brief contains different procedures to calculate and show diagnostic info.
 */

#ifndef DIAGNOSTIC_H
#define DIAGNOSTIC_H

#include "Blob.h"
#include "data_processing.h"
#include "image_processing.h"
#include "Context.h"
#include "build_def.h"

#ifndef WIN32
#ifdef __cplusplus
extern "C"
{
#endif
#endif

#ifdef CALC_DIAGNOSTIC_INFO

//! Allocate default empty diagnostic information.
_MarkDiagnostic allocate_empty_diagnostic_info(int height, int width, _SymbolDiagnostic ****symbols);

//! Get diagnostic info from found and recognized symbols on the image.
_MarkDiagnostic get_diagnostic_info(bool diagnostics, RecMark recMark, CvPoint cropMove,
                                double resizeCoef, AlignInfo alignInfo, 
                                            _SymbolDiagnostic ****symbols);

//! Choose diagnostic points having info about default points and angle value (can be 0, 90, 180 or 270).
void choose_diagnostic_points(CvPoint *topLeft, CvPoint *downRight, int angle);

//! Apply shift and rotation operations to the point.
CvPoint transform_point(CvPoint in, CvPoint2D32f shift, CvMat *rotMatrix);

/*! Apply shift and rotation operations to the straight rectangle
 * which is built from two input points (top left and down right)
 * and receive output points as top left and down right corners of
 * the shifted and rotated rectnagle which was converted to straight
 * form.*/
void transform_point_pair(CvPoint p1In, CvPoint p2In,
                          CvPoint2D32f shift, CvMat *rotMatrix,
                          CvPoint *p1Out, CvPoint *p2Out);


//! Restore point after resize and crop transformations.
CvPoint resize_crop_restore(CvPoint in, double resizeCoef, CvPoint cropMove);

//! Draw diagnostic rectangle on the canvas.
void draw_diagnostic_rectangle(CvMat *canvas, CvScalar color, CvPoint upperLeft, CvPoint lowerRight, int angle);

//! Draw diagnostic info from context on canvas.
void show_diagnostic_info(CvMat *canvas, void *cxt);

#endif

#ifndef WIN32
#ifdef __cplusplus
}
#endif
#endif

#endif
