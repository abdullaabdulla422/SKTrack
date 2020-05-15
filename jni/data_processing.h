/*!\file data_processing.h
 * \brief contains different procedures for memory allocation/deallocation, sorting, statistics finding, 2D geometry functions and recognition data and procedures.
 */ 

#ifndef DATA_PROCESSING_H
#define DATA_PROCESSING_H

#ifndef WIN32
#ifndef __cplusplus
#include "stdbool.h"
//#define bool int
//#define true 1
//#define false 0
#endif
#endif

#include "stdlib.h"
#include "stdio.h"
#include "math.h"

#include <opencv2/core/core_c.h>
#include <opencv2/imgproc/imgproc_c.h>

#include "build_def.h"

#ifndef WIN32
#ifdef __cplusplus
extern "C"
{
#endif
#endif

// 1. Memory routines

//! Allocate memory for 2D integer matrix.
int **allocate_2d_int_matrix(int rows, int cols, int value);

//! Allocate memory for 2D double matrix.
double **allocate_2d_double_matrix(int rows, int cols, double value);

//! Free memory allocated for integer matrix.
void free_2d_int_matrix(int **matrix, int rows);

//! Free memory allocated for double matrix.
void free_2d_double_matrix(double **matrix, int rows);

// 2. Sorting structures and routines

//! Structure to sort saving info about indexes of values.
/** It contains two fields:
 *
1. value of double type - the value for sorting.

2. index of int type - index or any additional info about
the sorted element to identify it.
*/
typedef struct
{
    //! Value to sort.
    double value;

    //! Integer id (or index) of value
    int index;
} SortStruct;

//! Sort 1D-array of integers.
void sort1d(const int *values, int sz, int *sorted, int *indexes, bool order);

//! Comparator for sort1d() descending.
int _descend_compare(const void *a, const void *b);

//! Comparator for sort1d() ascending.
int _ascend_compare(const void *a, const void *b);

//! Having neighbouring vector and an array of coordinates get line ids.
int get_line_ids(const int *ngbVector, const double *crdVector,
                                                int *outId, int sz);

// 3. Statistical routines

//! Get average value of the array of integers.
double average(const int *values, int sz);

//! Get average value of the array of doubles.
double faverage(const double *values, int sz);

//! Get median value of the array of doubles.
double fmedian(const double *values, int sz);

//! Calculate modal value for array of integers.
int modal_value(const int *values, int sz);

//! Calculate maximal value and its index in array of integers.
void maximum_int_1d(const int *values, int sz, 
                                int *maxValue, int *maxValueInd);

//! Calculate maximal value and its index in array of doubles.
void maximum_double_1d(const double *values, int sz, 
                                double *maxValue, int *maxValueInd);

//! Calculate correlation between two float vectors.
float calc_correlation(const float *x, const float *y, unsigned sz);


// 4. Data for recognition

// Data symbol etalons.
/*!\var extern float Y_DATA_ZERO[25] 
*\brief Ideal 5x5 "zero" data symbol for 'Y' character.
*/
extern float Y_DATA_ZERO[25];

/*!\var extern float Y_DATA_ONE[25]
*\brief Ideal 5x5 "one" data symbol for 'Y' character.
*/
extern float Y_DATA_ONE[25];

/*!\var extern float Y_DATA_TWO[25]
 *\brief Ideal 5x5 "two" data symbol for 'Y' character.
 */
extern float Y_DATA_TWO[25];

/*!\var extern float Y_DATA_THREE[25]
 *\brief Ideal 5x5 "three" data symbol for 'Y' character.
 */
extern float Y_DATA_THREE[25];
 
 /*!\var extern float V_DATA_ZERO[25] 
*\brief Ideal 5x5 "zero" data symbol for 'V' character.
*/
extern float V_DATA_ZERO[25];

/*!\var extern float V_DATA_ONE[25]
*\brief Ideal 5x5 "one" data symbol for 'V' character.
*/
extern float V_DATA_ONE[25];

/*!\var extern float V_DATA_TWO[25]
 *\brief Ideal 5x5 "two" data symbol for 'V' character.
 */
extern float V_DATA_TWO[25];

/*!\var extern float V_DATA_THREE[25]
 *\brief Ideal 5x5 "three" data symbol for 'V' character.
 */
extern float V_DATA_THREE[25];

// Orientation symbol etalons.
/*!\var extern float Y_ORIENT_UPRIGHT[25]
 *\brief Ideal 5x5 upright orientation symbol for 'Y' character.
 */
extern float Y_ORIENT_UPRIGHT[25];

/*!\var extern float Y_ORIENT_UPLEFT[25]
 *\brief Ideal 5x5 upleft orientation symbol for 'Y' character.
 */
extern float Y_ORIENT_UPLEFT[25];

/*!\var extern float Y_ORIENT_DOWNLEFT[25]
 *\brief Ideal 5x5 downleft orientation symbol for 'Y' character.
 */
extern float Y_ORIENT_DOWNLEFT[25];

/*!\var extern float Y_ORIENT_DOWNRIGHT[25]
 *\brief Ideal 5x5 downright orientation symbol for 'Y' character.
 */
extern float Y_ORIENT_DOWNRIGHT[25];

/*!\var extern float V_ORIENT_UPRIGHT[25]
 *\brief Ideal 5x5 upright orientation symbol for 'V' character.
 */
extern float V_ORIENT_UPRIGHT[25];

/*!\var extern float V_ORIENT_UPLEFT[25]
 *\brief Ideal 5x5 upleft orientation symbol for 'V' character.
 */
extern float V_ORIENT_UPLEFT[25];

/*!\var extern float V_ORIENT_DOWNLEFT[25]
 *\brief Ideal 5x5 downleft orientation symbol for 'V' character.
 */
extern float V_ORIENT_DOWNLEFT[25];

/*!\var extern float V_ORIENT_DOWNRIGHT[25]
 *\brief Ideal 5x5 downright orientation symbol for 'V' character.
 */
extern float V_ORIENT_DOWNRIGHT[25];

// Data symbol ids.
/*!\var extern int DATA_ZERO_ID 
 *\brief "Zero" data symbol id.
 */
extern int DATA_ZERO_ID;

/*!\var extern int DATA_ONE_ID 
 *\brief "One" data symbol id.
 */
extern int DATA_ONE_ID;

/*!\var extern int DATA_TWO_ID 
 *\brief "Two" data symbol id.
 */
extern int DATA_TWO_ID;

/*!\var extern int DATA_THREE_ID 
*\brief "Three" data symbol id.
*/
extern int DATA_THREE_ID;

// Orientation symbol ids.
/*!\var extern int ORIENT_UPRIGHT_ID
*\brief Upright orientation symbol id.
*/
extern int ORIENT_UPRIGHT_ID;

/*!\var extern int ORIENT_UPLEFT_ID
*\brief Upleft orientation symbol id.
*/
extern int ORIENT_UPLEFT_ID;

/*!\var extern int ORIENT_DOWNLEFT_ID
 * \brief Downleft orientation symbol id.
 */
extern int ORIENT_DOWNLEFT_ID;

/*!\var extern int ORIENT_DOWNRIGHT_ID
*\brief Downright orientation symbol id.
*/
extern int ORIENT_DOWNRIGHT_ID;

// 5. Pseudogeometrical matrix transformation routines 

//! Rotate 2D matrix of integers on 90 degrees.
void rotate_matrix_90_degrees(int ***matrix, int w, int h);

//! Update integers in the matrix corresponding to the number of rotations.
void update_matrix_values_after_rotation(int **matrix,
                                            int w, int h, int numRot);


// 6. 2D geometry structures and routines

//! Euclidian distance between two points.
double dist(CvPoint2D32f p1, CvPoint2D32f p2);

//! Calculate distances between points.
double **calculate_distances(CvPoint2D32f *points, int sz);

//! Structure to save 2D line parameters.
/** Line structure represents line on the 2D plane.
It contains the following fields:

1. px of float type - x-coordinate of the arbitrary point on the plane.

2. py of float type - y-coordinate of this point on the plane.

3. vx - x-component of the vector collinear to the line.

4. vy - y-component of the vector collinear to the line.
*/
typedef struct
{
    //! x-coordinate of some point on the line.
    float px;

    //! y-coordinate of the same point on the line.
    float py;

    //! x-coordinate of vector collinear to the line.
    float vx;

    //! y-coordinate of vector collinear to line.
    float vy;
} Line;

//! Get line equation from the arrays of coordinates.
Line get_line_equation(float *x, float *y, int sz);

//! Find intersection point of two lines.
CvPoint2D32f *get_lines_intersection(Line line1, Line line2, float eps);

#ifndef WIN32
#ifdef __cplusplus
}
#endif
#endif

#endif
