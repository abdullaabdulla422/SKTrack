/*! \file image_processing.h
 *\brief contains image processing procedures.
 */

#ifndef IMAGE_PROCESSING_H
#define IMAGE_PROCESSING_H

#include "time.h"
#include "stdlib.h"
#include "stdio.h"

#include "data_processing.h"
#include <opencv2/highgui/highgui_c.h>
#include "Blob.h"
#include "Context.h"
#include "build_def.h"

#ifndef WIN32
#ifdef __cplusplus
extern "C"
{
#endif
#endif


// 1. Low-level image operations

//! Convert raw image data to CvMat.
int get_cvmat_from_raw_data(CvMat *image, int rows, int cols, int format,
                            const unsigned char *rawData);
//! Invert image.
CvMat *invert_image(const CvMat *source);


// 2. Structures and routines to binarize and align image, receiving set of blobs

//! Binarize an image to left only white mark symbols on the black background.
CvMat *binarize_image(const CvMat *in, int blockSize, double shiftValue,
                    double minBlobSize, double maxBlobSize, 
                    double maxBlobSideRatio, double minBlobDensity,
                    double maxBlobDensity, double maxGeomRegParam);

//! Structure to store binary image alignment results.
/** AlignInfo structure represents the necessary info after binary image alignement.
It contains the following fields:

1. alignedImage of CvMat * type - result of alignment.

2. mapMatrix of CvMat * type - 2x3 CV_32FC1 (single channel float) matrix which contains
					the affine transformation parameters corrseponding to made alignment.

3. alignAngle if int type - extracted angle value of rotation from [0;89] integer range. 
*/
typedef struct
{
    //! Aligned image.
    CvMat *alignedImage;

    //! Matrix of affine transform (2x3) to allign.
    CvMat *mapMatrix;

    //! Angle of alignment.
    int alignAngle;
} AlignInfo;

//! Get minimal rectangular area around the symbol on the image.
CvBox2D get_min_area(const CvMat *image, CvRect area);

//! Align binary image with mark to be upright.
AlignInfo align_binary_image(const CvMat *image);

// 3. Single image recognition routines

//! Get mask templates for specified symbol.
float **get_symbol_templates(const char *symbolName);

//! Recognize the type (data, orientation, broken) of symbol.
int recognize_symbol_type(const CvMat *symbolImage, const float **templates,
                                                    float brokenTypeThresh);

//! Recognize data symbol using per-pixel correlation.
int recognize_data_symbol(const CvMat *symbolImage, const float **templates);

//! Recognize orientation symbol using per-pixel correlation.
int recognize_orient_symbol(const CvMat *symbolImage, const float **templates);

//! Extract normalized features for correlation function from the image.
void get_feature_vector_from_image(const CvMat *symbolImage, float *data);

// 4. Segmented mark processing structures and routines

//! Structure to store recognition results.
/** RecMark structure represents low-level recognition results with 
extra auxiliary info and contains the following fields:

1. symbols of BlobArray type - the filtered array of blobs.

2. width of int type - width of the mark in symbols.

3. height of int type - height of the mark in symbols.

4. result of int** type - matrix of recognition results for data symbols.

5. indexes of int** type - matrix of indexes in the symbols BlobArray for
		found data and oriented symbols.

6. error of int type - integer value which corresponds to the processing 
						error  type.

7. values of int* type - diagnostic array of recognition values.

8. types of bool* type - diagnostic array of recognezed symbol types
					(data, oriented or corrupted).

9. focus of CvPoint type - the focus of mark on the image.

10. upperLeft of CvPoint* type - diagnostic array of upper left points
						for symbols pixel frames.

11. lowerRight of CvPoint* type - diagnostic array of lower right points
						for symbols pixel frames.

12. angle of int type - may be 0, 90, 180 or 270 and equals to the number of degrees
						for rotation of aligned binary image to make mark upright.

Fields 7-12 exist only if flag CALC_DIAGNOSTIC_INFO is defined.
*/
typedef struct
{
    // main info:
    //! Array of segmented and recognized blobs.
    BlobArray symbols;

    //! Width of mark in data symbols (should be even number).
    int width;

    //! Height of mark in data symbols (should be even number).
    int height;

    //! width x height matrix of recognition results for data symbols.
    int **result;

    /*! (width + 1) x (height + 1) matrix of indexes for data and
    * orientation symbols in the array of blobs.*/
    int **indexes;

    //! Translation error code.
    int error;

#ifdef CALC_DIAGNOSTIC_INFO
    // additional info:
    //! Diagnostic array of values.
    int *values;

    //! Dignostic array of symbol types (true - data, false - orientation).
    bool *types;
    
    //! Focus coordinates.
    CvPoint focus;

    //! Array of upper left points.
    CvPoint *upperLeft;

    //! Array of lower right points.
    CvPoint *lowerRight;

    //! Angle of rotation (may be 0, 90, 180, 270 or -1).
    int angle;

#endif

} RecMark;

//! Find focus on the partial matrix.
CvPoint find_focus_on_partial_matrix(int **partialMatrix, int rows, int cols, int numThresh);

//! Smart arrange of blobs on 2D grid.
CvPoint *smart_arrange_blobs(BlobArray blAr, int *rows, int *cols, double arrangeThresh);

//! Translate the array of blobs to the mark.
RecMark translate_blobs_to_mark(BlobArray blAr, Context *cxt);

//! Calculate additional mark info.
void calculate_mark_additional_info(RecMark *recMark);

#ifndef WIN32
#ifdef __cplusplus
}
#endif
#endif

#endif
