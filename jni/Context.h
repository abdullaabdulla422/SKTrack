/*!\file Context.h
 * \brief contains Context structure and connected structures definition.
 */

#ifndef CONTEXT_H
#define CONTEXT_H

#include "build_def.h"

#ifndef WIN32
    #ifndef __cplusplus
#include "stdbool.h"
    #endif
#endif

#ifndef WIN32
#ifdef __cplusplus
extern "C"
{
#endif
#endif

//! Enumeration type to represent translation results.
enum TranslateResult
{
    //! Successful translation.
    SUCCESS = 0,

    //! Translate has not been executed.
    NO_RESULT = -1,

    //! Bad algorithm parameter.
    BAD_PARAMETER = -2,

    //! No image to process.
    NO_IMAGE = -3,

    //! Symbols were not found.
    NO_SYMBOLS = -4,

    //! Focus was not found.
    NO_FOCUS = -5
};

//! Type redefinition for TranslateResult structure.
typedef enum TranslateResult TResult;

//! Structure to represent 2D integer point.
typedef struct
{
    //! x-coordinate.
    int x;

    //! y-coordinate.
    int y;
    
} _Point;

//! Return point given two integers.
_Point _point(int x, int y);

//! Structure to represent 2D integer size.
typedef struct
{
    //! Width.
    int width;

    //! Height.
    int height;

} _Size;

//! Return size given two integers.
_Size _size(int width, int height);

//! Structure to represent input bitmap info.
/** It contains the following fields:
 *
1. height of int type - height of image in pixels

2. width of int type - width of image in pixels

3. stride of int type - number of bytes in the image's single row.

4. format of int type - 0 if 24RGB, 1 if 32RGB data image format.
*/
typedef struct
{
    //! Height (number of rows) in pixels.
    int height;

    //! Width (number of columns) in pixels.
    int width;

    //! Number of bytes in single row.
    int stride;

    //! 0 if 24bit RGB, 1 if 32bit RGBA.
    int format;

} BitmapInfo;


//! Structure to represent computer vision algorithm settings.
/**
Contains the settings of the following algorithms:

I. Gray scale image binarization:

1. segmBlockSize of int type - the size of segmentation block.

2. segmShiftValue of double type - the shift value for threshold level.

II. Blob filtering:

3. minBlobSize of double type - the minimal blob size not to filter

4. maxBlobSize of double type - the maximal blob size not to filter

5. maxBlobSideRatio of double type - the maximal ratio between 
					maximal and minimal sides of blob not to filter

6. minBlobDensity of double type - the minimal blob density not to filter

7. maxBlobDensity of double type - the maximal blob density not to filter

8. maxGeomRegParam of double type - for details see "Blob.c": filter_blobs_with_geometry_regularity() function.

9. criticalNgbAreaRatios of double type - for details see "Blob.c":filter_blobs_on_grid() function.

III. Symbol recognition:

10. badSymbolRecThresh of double type - during symbol recognition minimal possible value of correlation with
								the nearest ideal symbol, if the correlation value is less than the given value,
								symbol is treated as corrupted.

IV. Symbols arrangement:

11. findFocusNumThresh - minimal number of symbols in row or column recognized as oriented during focus finding.

12. arrangeThresh of double type - for details see "Blob.c":look_for_connections() function.

V. Service info

13. areOptionsSet of bool type - changing it user accepts or declines whether all other options are set correctly.

*/
typedef struct
{
    //! Segmentation algorithm: block size - should be an odd positive number.
    int segmBlockSize;

    //! Segmentation algorithm: intensity shift value for segmenting.
    double segmShiftValue;

    //! Filters: minimal size of blob.
    double minBlobSize;

    //! Filters: maximal size of blob.
    double maxBlobSize;

    //! Filters: maximal ratio between longest and shortest blob axes.
    double maxBlobSideRatio;

    //! Filters: minimal blob filling density.
    double minBlobDensity;

    //! Filters: maximal blob filling density.
    double maxBlobDensity;

    //! Filters: maximal geometry regularity parameter.
    double maxGeomRegParam;

    //! Filters: critical value for neighbouring blob areas. 
    double criticalNgbAreaRatio;

    //! Threshold value for symbol recognition to determine bad symbol.
    double badSymbolRecThresh;

	//! Minimal number of symbols on row or column recognized as oriented during focus finding.
	int findFocusNumThresh;

    //! Threshold value for blobs arrangement.
    double arrangeThresh;

    //! Flag, is true if all options are verified by user.
    bool areOptionsSet;

} AlgSettings;

#ifdef CALC_DIAGNOSTIC_INFO
//! Structure to present diagnostic info about single found symbol.
typedef struct
{
    //! Is current symbol data or oriented.
    bool isDataSymbol;

    //! Top left point of symbol on the image.
    _Point frameUpperLeft;

    //! Bottom right point of symbol on the image.
    _Point frameLowerRight;

    //! Recognized value of symbol.
    unsigned char value;

} _SymbolDiagnostic;

//! Structure to present dignostic info about found mark.
typedef struct
{
    //! Coordinates of the mark focus on the image.
    _Point focus;

    //! Coordinates of the top left point of the mark on the image.
    _Point frameUpperLeft;

    //! Coordinates of the bottom right point of the mark on the image.
    _Point frameLowerRight;

    //! Approximate angle of rotation, in degrees, -1 if difficult to obtain.
    int angle;
} _MarkDiagnostic;
#endif


//! Structure to represent translation context.
typedef struct
{
    /*! True, if it's need to recognize light symbols on the dark background,
     * false, if it's need to recognize dark symbols on the light background.
     */
    bool lightOnDark;

    //! Torn on/off calculation of diagnostic info.
    bool diagnostics;

    //! Is input image flipped or not.
    bool flipped;

    //! Height of mark in symbols (number of rows).
    int height;

    //! Width of mark in symbols (number of columns).
    int width;

    //! Number of missing symbols during translation.
    int missing;

    //! Number of total found symbols (including orientation symbols).
    int foundSymbolsNumber;

    //! Right lower point of rectangular ROI on the image.
    _Point regionLowerRight;

    //! Left upper point of rectangular ROI on the image.
    _Point regionUpperLeft;

    //! Focus name - constant value at the moment ("Symbol").
    char focusName[80];

    //! Symbol name - constant value at the moment ('Y').
    char symbolName[80];

#ifdef CALC_DIAGNOSTIC_INFO
    //! Diagnostic info about mark.
    _MarkDiagnostic foundMark;

    //! Array of diagnostic info structures about each found symbol.
    _SymbolDiagnostic ***foundSymbols;
#endif

    //! Bitmap info.
    BitmapInfo bitmapInfo;

    /*! Optimal size of bitmap to translate, if the area of bitmap is bigger,
         than area of optimal bitmap, given bitmap will be resized; the growing
         of optimal size can cause deceleration of translation process, but also
         it can grow recognition quality.
    */  
    _Size optimalBitmap;

    //! Settings of computer vision algorithms.
    AlgSettings algSettings;

    //! Translation result.
    TResult resultCode;

    //! Translationr result message. 
    char resultMessage[80];

} Context;

#ifndef WIN32
#ifdef __cplusplus
}
#endif
#endif


#endif
