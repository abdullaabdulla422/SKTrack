/*!\file Blob.h
 * \brief contains Blob structure definition and connected procedures.
 */

#ifndef BLOB_H
#define BLOB_H

#include <float.h>

#include "data_processing.h"
#include "build_def.h"

#ifndef WIN32
#ifdef __cplusplus
extern "C"
{
#endif
#endif

//! Structure to store results for single connected component.
/**
Blob structure stores info which corresponds to single found connected component
named "Blob" on the binary image.

It contains the following fields:

1. frarea of CvRect type - describes rectangular subframe on the image where
		the given blob belongs to.

2. mask of CvMat* type - binary mask which is equal to the size of frarea. It corresponds
		to the subframe where blob was found. The OpenCV type of data inside the mask is 
		8UC1 (single 8 bit channel). If pixel of the mask has a value which differs from 0,
		the corresponding pixel on the original binary image belongs to the current blob,
		otherwise it doesn't belong to the blob.

3. mrarea of CvBox2D type - represents the minimal oriented rectangle on the original image
		which all blob's pixels belong to.
*/
typedef struct
{
    //! Rectangular subframe which corresponds to blob.
    CvRect frarea;
    
    //! Binary mask for connected component.
    CvMat *mask;

    /*! Oriented rectangle with minimal area which includes all points 
     * of connected component.
     */
    CvBox2D mrarea;

} Blob;

//! Structure to store array of connected components.
/**
BlobArray structure is made to emulate simple array of Blob variables.

It contains the following fields:

1. blobs  of Blob * type - pointer to the first 0-based element of the 
C-style array of Blob variables.

2. sz of unsigned int type - the size of  emulated array.
*/
typedef struct
{
    //! Array of blobs.
    Blob *blobs;
    
    //! The size of array above.
    unsigned sz;
} BlobArray;

//! Find blobs on the image.
BlobArray find_blobs(const CvMat *image);

//! Draw blobs on the canvas.
void draw_blobs(BlobArray blobs, CvMat *canvas);

//! Fill single contour with the given color on canvas.
void fill_single_contour(CvSeq *contour, CvPoint offset,
        CvScalar interiorColor, CvScalar contourColor,
        CvMat *canvas);

//! Filter array of blobs with size.
void filter_blobs_with_size(BlobArray *blAr, double minSize, double maxSize);

//! Filter array of blobs with side ratio.
void filter_blobs_with_side_ratio(BlobArray *blAr, double maxRatio);

//! Filter array of blobs with density.
void filter_blobs_with_density(BlobArray *blAr, double minDensity,
                                                double maxDensity);

//! Filter array of blobs with geometry regularity.
void filter_blobs_with_geometry_regularity(BlobArray *blAr, double maxRelDev);

//! Filter array of blobs with their indexes on 2D grid.
void filter_blobs_on_grid(BlobArray *blAr, const CvPoint *gridIndexes,
                          int rows, int cols, double threshValue);

//! Release single blob.
void release_blob(Blob *blob);

//! Release an array of blobs.
void release_blob_array(BlobArray *blAr);

//! Calculate degree of is blob2 is left neighbour of blob1.
double isLeftNeighbour(Blob b1, Blob b2);

//! Calculate degree of is blob2 is right neighbour of blob1.
double isRightNeighbour(Blob b1, Blob b2);

//! Calculate degree of is blob2 is top neighbour of blob1.
double isTopNeighbour(Blob b1, Blob b2);

//! Calculate degree of is blob2 is down neighbour of blob1.
double isDownNeighbour(Blob b1, Blob b2);

//! Look for connections for single blob.
int look_for_connections(double (*funcPtr)(Blob, Blob),
                         int *ngbIndexes, BlobArray blAr,
                         int searchElemInd, double arrangeThresh);
#ifndef WIN32
#ifdef __cplusplus
}
#endif
#endif

#endif
