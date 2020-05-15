#include "data_processing.h"

/*!\fn sort1d(const int *values, int sz, int *sorted, int *indexes, bool order)
 * \param [in] values - array of input values.
 * \param [in] sz - the number of values.
 * \param [out] sorted - array of the output sorted values 
 *                       (memory must be allocated).
 * \param [out] indexes - array of the indexes of sorted values  
 *                        in the input array (memory must be allocated).
 * \param [in] order - boolean variable (order type), 
 *                     if true than descending order,
 *                     if false than ascending order.
 */
void sort1d(const int *values, int sz, int *sorted, int *indexes, bool order)
{
    // quick sort
    if (values == NULL || (sorted == NULL && indexes == NULL))
        return;

    SortStruct *data = (SortStruct*)malloc(sz * sizeof(SortStruct));
    for (int i = 0; i < sz; i++)
    {
        data[i].value = values[i];
        data[i].index = i;
    }
    
    // descending
    if (order)
        qsort(data, sz, sizeof(SortStruct), _descend_compare);
    // ascending
    else
        qsort(data, sz, sizeof(SortStruct), _ascend_compare);

    // initialize output arrays
    if (sorted != NULL)
        for (int i = 0; i < sz; i++)
            sorted[i] = (int)data[i].value;
    
    if (indexes != NULL)
        for (int i = 0; i < sz; i++)
            indexes[i] = data[i].index;

    free(data);
}


/*!\fn _descend_compare(const void *a, const void *b)
 * \param [in] a - pointer to the first value to compare.
 * \param [in] b - pointer to the second value to compare.
 * \return value which sign corresponds to the descending sort order.
 */
int _descend_compare(const void *a, const void *b)
{
    return (int)(((SortStruct *)b)->value - ((SortStruct *)a)->value);
}


/*!\fn _ascend_compare(const void *a, const void *b)
 * \param [in] a - pointer to the first value to compare.
 * \param [in] b - pointer to the second value to compare.
 * \return value which sign corresponds to the ascending sort order.
 */
int _ascend_compare(const void *a, const void *b)
{
    return (int)(((SortStruct *)a)->value - ((SortStruct *)b)->value);
}


/*!\fn average(const int *values, int sz)
 * \param [in] values - input array of integers.
 * \param [in] sz - the size of input array.
 * \return average value of array.
 *
 * Calculate average value of the array of integers.
 */
double average(const int *values, int sz)
{
    if (sz < 1)
        return 0;

    double sum = 0.0;
    for (int i = 0; i < sz; i++)
        sum += values[i];

    return sum/sz;
}


/*!\fn faverage(const double *values, int sz)
 * \param [in] values - input array of doubles.
 * \param [in] sz - the size of input array.
 * \return average value of array.
 *
 * Calculate average value of the array of doubles.
 */
double faverage(const double *values, int sz)
{
    if (sz < 1)
        return 0;

    double sum = 0.0;
    for (int i = 0; i < sz; i++)
        sum += values[i];

    return sum/sz;
}

/*!\fn fmedian(const double *values, int sz)
 * \param [in] values - inpput array of doubles.
 * \param [in] sz - size of the input array.
 * \return median value of array.
 *
 * Calculate median value of the array of doubles.
 */
double fmedian(const double *values, int sz)
{
    if (sz < 1)
        return 0;
    
    // sort given values
    SortStruct *data = (SortStruct*)malloc(sz * sizeof(SortStruct));
    
    for (int i = 0; i < sz; i++)
    {
        data[i].value = values[i];
        data[i].index = i;
    }

    qsort(data, sz, sizeof(SortStruct), _ascend_compare);
    
    // calculate median
    double medianValue = 0;

    if (sz%2 == 0)
        medianValue = 0.5*(data[sz/2].value + data[sz/2 - 1].value);
    else
        medianValue = data[(sz-1)/2].value;

    // free allocated memory
    free(data);

    return medianValue;
}



/*!\fn calc_correlation(const float *x, const float *y, unsigned sz)
 * \param [in] x - first float vector.
 * \param [in] y - second float vector.
 * \param [in] sz - size of vectors.
 * \return correlation coefficient.
 *
 * Calculate correlation between two float vectors.
 * To apply this function for recognition it must be taken into account
 * that all elements of vectors should be in [-1;1] segment.
 */
float calc_correlation(const float *x, const float *y, unsigned sz)
{
    if (sz < 1)
        return 0;

    float sum = 0;
    for (unsigned i = 0; i < sz; i++)
        sum += x[i]*y[i];
    
    return sum/sz;
}

// Data for symbol recognition.
float Y_DATA_ZERO[25] = 
                       {-1., -1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.,
                        -1., +1., -1., +1., -1.,
                        +1., -1., -1., -1., +1.};

float V_DATA_ZERO[25] =
                        {-1., -1., +1., -1., -1.,
                         -1., +1., -1., +1., -1.,
                         -1., +1., -1., +1., -1.,
                         +1., -1., -1., -1., +1.,
                         +1., -1., -1., -1., +1.};

float Y_DATA_ONE[25] =
                       {+1., -1., -1., -1., -1.,
                        -1., +1., -1., -1., -1.,
                        -1., -1., +1., +1., +1.,
                        -1., +1., -1., -1., -1.,
                        +1., -1., -1., -1., -1.};
float V_DATA_ONE[25] = 
                        {+1., +1., -1., -1., -1.,
                         -1., -1., +1., +1., -1.,
                         -1., -1., -1., -1., +1.,
                         -1., -1., +1., +1., -1.,
                         +1., +1., -1., -1., -1.};
                        
float Y_DATA_TWO[25] =
                       {+1., -1., -1., -1., +1.,
                        -1., +1., -1., +1., -1.,
                        -1., -1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.};
float V_DATA_TWO[25] = 
                        {+1., -1., -1., -1., +1.,
                         +1., -1., -1., -1., +1.,
                         -1., +1., -1., +1., -1.,
                         -1., +1., -1., +1., -1.,
                         -1., -1., +1., -1., -1.};
                       
float Y_DATA_THREE[25] = 
                       {-1., -1., -1., -1., +1.,
                        -1., -1., -1., +1., -1.,
                        +1., +1., +1., -1., -1.,
                        -1., -1., -1., +1., -1.,
                        -1., -1., -1., -1., +1.};

float V_DATA_THREE[25] = 
                        {-1., -1., -1., +1., +1.,
                         -1., +1., +1., -1., -1.,
                         +1., -1., -1., -1., -1.,
                         -1., +1., +1., -1., -1.,
                         -1., -1., -1., +1., +1.};

float Y_ORIENT_UPRIGHT[25] =
                       {+1., -1., -1., -1., -1.,
                        -1., +1., -1., -1., -1.,
                        -1., -1., +1., +1., +1.,
                        -1., -1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.};

float V_ORIENT_UPRIGHT[25] = 
                        {+1., +1., +1., -1., -1.,
                         +1., -1., -1., +1., -1.,
                         +1., -1., -1., -1., +1.,
                         -1., +1., -1., -1., -1.,
                         -1., -1., +1., -1., -1.};

float Y_ORIENT_UPLEFT[25] =
                       {-1., -1., -1., -1., +1.,
                        -1., -1., -1., +1., -1.,
                        +1., +1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.};

float V_ORIENT_UPLEFT[25] =
                        {-1., -1., +1., +1., +1.,
                         -1., +1., -1., -1., +1.,
                         +1., -1., -1., -1., +1.,
                         -1., -1., -1., +1., -1.,
                         -1., -1., +1., -1., -1.};

float Y_ORIENT_DOWNLEFT[25] =
                       {-1., -1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.,
                        +1., +1., +1., -1., -1.,
                        -1., -1., -1., +1., -1.,
                        -1., -1., -1., -1., +1.};

float V_ORIENT_DOWNLEFT[25] = 
                        {-1., -1., +1., -1., -1.,
                         -1., -1., -1., +1., -1.,
                         +1., -1., -1., -1., +1.,
                         -1., +1., -1., -1., +1.,
                         -1., -1., +1., +1., +1.};

float Y_ORIENT_DOWNRIGHT[25] =
                       {-1., -1., +1., -1., -1.,
                        -1., -1., +1., -1., -1.,
                        -1., -1., +1., +1., +1.,
                        -1., +1., -1., -1., -1.,
                        +1., -1., -1., -1., -1.};

float V_ORIENT_DOWNRIGHT[25] =
                        {-1., -1., +1., -1., -1.,
                         -1., +1., -1., -1., -1.,
                         +1., -1., -1., -1., +1.,
                         +1., -1., -1., +1., -1.,
                         +1., +1., +1., -1., -1.};

// Data for symbol identification.
int DATA_ZERO_ID = 0;
int DATA_ONE_ID = 2;
int DATA_TWO_ID = 4;
int DATA_THREE_ID = 6;

int ORIENT_UPRIGHT_ID = 7;
int ORIENT_UPLEFT_ID = 1;
int ORIENT_DOWNLEFT_ID = 3;
int ORIENT_DOWNRIGHT_ID = 5;

/*!\fn dist(CvPoint2D32f p1, CvPoint2D32f p2)
 * \param [in] p1 - first point.
 * \param [in] p2 - second point.
 * \return Euclidian distance between them.
 */
double dist(CvPoint2D32f p1, CvPoint2D32f p2)
{
    return sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
}


/*! \fn calculate_distances(CvPoint2D32f *points, int sz)
 * \param [in] points - array of points.
 * \param [in] sz - the number of points (array's size).
 * \return 2D matrix of distances between them.
 */
double **calculate_distances(CvPoint2D32f *points, int sz)
{
    double **distances = (double**)malloc(sz * sizeof(double *));
    for (int i = 0; i < sz; i++)
        distances[i] = (double*)malloc(sz * sizeof(double));

    for (int i = 0; i < sz; i++)
        for (int j = i; j < sz; j++)
        {
            double d = dist(points[i], points[j]);
            distances[i][j] = d;
            distances[j][i] = d;

        }

    return distances;
}


/*! \fn modal_value(const int *values, int sz)
 * \param [in] values - array of integers.
 * \param [in] sz - the size of array above.
 * \return modal value of array.
 *
 * Calculate modal value for the array of integers.
 */
int modal_value(const int *values, int sz)
{
    if (sz < 1)
        return 0;

    // create array for cointers
    int *counts = (int*)malloc(sz * sizeof(int));

    for (int i = 0; i < sz; i++)
        counts[i] = 0;

    // calculate counters
    for (int i = 0; i < sz; i++)
        counts[values[i]]++;

    //find maximal counter
    int maxInd = 0;
    int maxValue = counts[0];

    for (int i = 1; i < sz; i++)
        if (maxValue < counts[i])
        {
            maxInd = i;
            maxValue = counts[i];
        }
    
    // free allocated memory
    free(counts);

    return maxInd;
}


/*!\fn rotate_matrix_90_degrees(int ***matrix, int w, int h)
 * \param [in,out] matrix - pointer to 2D matrix to rotate.
 * \param [in] w - width of the matrix above.
 * \param [in] h - height of the matrix above.
 *
 * Given 2D matrix of integers with certain width and height aka 
 * the 2D array of integers, rotate it 90 degrees clockwise.
 */
void rotate_matrix_90_degrees(int ***matrix, int w, int h)
{
    // allocate memory for output matrix
    int **out = allocate_2d_int_matrix(w, h, -1);

	// fill output matrix
    for (int i = 0; i < w; i++)
        for (int j = 0; j < h; j++)
            out[i][j] = (*matrix)[h - 1 - j][i];

    // free input matrix
	// TODO: fix the case w != h
    free_2d_int_matrix(*matrix, h);
	
    *matrix = out;
}


/*!\fn update_matrix_values_after_rotation(int **matrix, 
                                        int w, int h, int numRot)
* \param [in] matrix - 2D matrix of integers.
* \param [in] w - number of rows in the matrix.
* \param [in] h - the number of columns in the matrix.
* \param [in] numRot - the number of rotations to take into account.
*
* Given the rotated numRot times 90 degrees clockwise 2D matrix of integers with certain width and height aka 
* the 2D array of integers, update its values corresponding the number of rotations.
*/
void update_matrix_values_after_rotation(int **matrix, 
                                        int w, int h, int numRot)
{
    for (int i = 0; i < w; i++)
        for (int j = 0; j < h; j++)
            if (matrix[i][j] != -1)
                matrix[i][j] = (matrix[i][j] + numRot)%4;
}




/*!\fn allocate_2d_int_matrix(int rows, int cols, int value)
 * \param [in] rows - number of rows.
 * \param [in] cols - number of columns.
 * \param [in] value - value to fill with.
 * \return allocated matrix.
 */
int **allocate_2d_int_matrix(int rows, int cols, int value)
{
    int **out = (int**)malloc(rows * sizeof(int*));

    for (int i = 0; i < rows; i++)
        out[i] = (int*)malloc(cols * sizeof(int));

    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
            out[i][j] = value;

    return out;
}


/*!\fn allocate_2d_double_matrix(int rows, int cols, double value)
 * \param [in] rows - number of rows.
 * \param [in] cols - number of columns.
 * \param [in] value - value to fill with.
 * \return allocated matrix.
 */
double **allocate_2d_double_matrix(int rows, int cols, double value)
{
    double **out = (double**)malloc(rows * sizeof(double*));

    for (int i = 0; i < rows; i++)
        out[i] = (double*)malloc(cols * sizeof(double));

    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
            out[i][j] = value;

    return out;
}


/*!\fn free_2d_int_matrix(int **matrix, int rows)
 * \param [in] matrix - matrix to free.
 * \param [in] rows - rows number in the matrix.
 */
void free_2d_int_matrix(int **matrix, int rows)
{
    if (matrix == NULL)
        return;

    for (int i = 0; i < rows; i++)
		free(matrix[i]);

    free(matrix);
}


/*!\fn free_2d_double_matrix(double **matrix, int rows)
 * \param [in] matrix - matrix to free.
 * \param [in] rows - rows number in the matrix.
 */
void free_2d_double_matrix(double **matrix, int rows)
{
    if (matrix == NULL)
        return;
        
    for (int i = 0; i < rows; i++)
        free(matrix[i]);

    free(matrix);
}


/*!\fn maximum_int_1d(const int *values, int sz,
 *                                          int *maxValue, int *maxValueInd)
 * \param [in] values - input 1D-array of integers.
 * \param [in] sz - the number of elements in the input array.
 * \param [out] maxValue - maximal value in array.
 * \param [out] maxValueInd - index of maximal value in array.
 */
void maximum_int_1d(const int *values, int sz, 
                                            int *maxValue, int *maxValueInd)
{
    if (sz <= 0)
        return;

    *maxValue = values[0];
    *maxValueInd = 0;

    if (sz == 1)
        return;

    for (int i = 1; i < sz; i++)
        if (values[i] > *maxValue)
        {
            *maxValue = values[i];
            *maxValueInd = i;
        }
}


/*!\fn maximum_double_1d(const double *values, int sz,
 *                                          double *maxValue, int *maxValueInd)
 * \param [in] values - input 1D-array of doubles.
 * \param [in] sz - the number of elements in the input array.
 * \param [out] maxValue - maximal value in array.
 * \param [out] maxValueInd - index of maximal value in array.
 */
void maximum_double_1d(const double *values, int sz, 
                                          double *maxValue, int *maxValueInd)
{
    if (sz <= 0)
        return;

    *maxValue = values[0];
    *maxValueInd = 0;

    if (sz == 1)
        return;

    for (int i = 1; i < sz; i++)
        if (values[i] > *maxValue)
        {
            *maxValue = values[i];
            *maxValueInd = i;
        }
}


/*!\fn get_line_ids(const int *ngbVector, const double *crdVector, 
                                                int *outId, int sz)
* \param [in] ngbVector - vector of neighbour indexes for points.
* \param [in] crdVector - vector of point coordinates.
* \param [out] outId - output vector of line ids (should be allocated).
* \param [in] sz - the size of all vectors.
* \return total number of lines.
*
* Given the vector of neighbouring indexes and the vector of point coordinates,
* calculates for each point the line id corresponding to the neighbouring indexes.
* It's the main assumption that all points on the line can be found with the help
* of binary relations defined in the vector of neighbouring indexes. To order lines
* the average point value in the line is used, i.e. all lines should be collinear
* to the direction of maximal deviation of point coordinates in the particluar line.
* Also all lines are assumed to be collinear to each other.
*/
int get_line_ids(const int *ngbVector, const double *crdVector, 
                                                int *outId, int sz)
{
    // array of lines
    int **lines = NULL;
    // counter of lines
    int lineCounter = -1;
    // array of point counters for each horizontal line
    int *pointCounters = NULL;
    // array of classify flags
    int *classified = (int*)malloc(sz * sizeof(int));
    for (int i = 0; i < sz; i++)
        classified[i] = 0;
    // counter of classified points
    int classPointCounter = 0;
    
    //printf("Number of blobs is %d\n", sz);

    while (classPointCounter < sz)
    {
        //printf("ClassPointCounter is %d\n", classPointCounter);

        for (int i = 0; i < sz; i++)
        {
            // has element been already classified
            if (classified[i] == 1)
                continue;
            
            // is the first element of line
            if (ngbVector[i] == -1)
            {
                // increment number of lines
                lineCounter++;

                // reallocate memory for horizontal lines
                lines = (int**)realloc(lines, (lineCounter+1) * sizeof(int*));
                lines[lineCounter] = NULL;

                // reallocate memory for current line
                lines[lineCounter] = (int*)realloc(lines[lineCounter], 1 * sizeof(int));
                lines[lineCounter][0] = i;

                // reallocate memory for array of lengths of horizontal lines
                pointCounters = (int*)realloc(pointCounters, (lineCounter + 1) * sizeof(int));
                pointCounters[lineCounter] = 1;

                // modify corresponding flag and increment counter of
                // classified element
                classified[i] = 1;
                classPointCounter++;

                //printf("ClassPointCounter is incremented after line init: %d\n", classPointCounter);
            }

            else
            {
                // search line with the corresponding neighbour
                for (int j = 0; j < lineCounter + 1; j++)
                {
                    // if the last element of the line 
                    // is left neighbour of current item
                    if (lines[j][pointCounters[j] - 1] == ngbVector[i])
                    {
                        // increment the length of this line
                        pointCounters[j]++;

                        // reallocate memory for this line
                        lines[j] = (int*)realloc(lines[j],
                                        pointCounters[j] * sizeof(int));
                        lines[j][pointCounters[j] - 1] = i;

                        // modify corresponding flag and increment counter
                        // of classified element
                        classified[i] = 1;
                        classPointCounter++;

                        //printf("ClassPointCounter is incremented: %d\n", classPointCounter);
                    }
                }
            }
 
       }
    }
    
    //printf("Lines are built\n");
    // find average coordinates for each line 
    int *averageCoord = (int*)malloc((lineCounter+1) * sizeof(int));
    for (int i = 0; i < lineCounter + 1; i++)
    {
        double *x = (double*)malloc(pointCounters[i] * sizeof(double));
        for (int j = 0; j < pointCounters[i]; j++)
            x[j] = crdVector[lines[i][j]];

        averageCoord[i] = (int)faverage(x, pointCounters[i]);
        free(x);
    }
    
    // find line indexes 
    int *sorted = (int*)malloc((lineCounter + 1) * sizeof(int));
    int *sortIndexes = (int*)malloc((lineCounter + 1) * sizeof(int));
    sort1d(averageCoord, lineCounter + 1, sorted, sortIndexes, false);
    
    // for each sorted line
    for (int i = 0; i < lineCounter + 1; i++)
    {
        // get line index
        int hI = sortIndexes[i];

        // for each point in this line
        for (int j = 0; j < pointCounters[hI]; j++)
        {
            // get point index
            int pI = lines[hI][j];

            // set corresponding output value to the index of sorted line
            outId[pI] = i;
        }
    }

    // free allocated memory
    free(sortIndexes);
    free(averageCoord);
    free(sorted);
    free(classified);
    for (int i = 0; i < lineCounter + 1; i++)
        free(lines[i]);
    free(lines);
    free(pointCounters);

    return lineCounter + 1;
}


/*!\fn get_line_equation(float *x, float *y, int sz)
 * \param [in] x - array of x-coordinates.
 * \param [in] y - array of y-coordinates.
 * \param [in] sz - the size of arrays above.
 * \return Line structure.
 *
 * Find Line structure which approximates array of points given as
 * array of x-coordinates with the array of y-coordinates.
 */
Line get_line_equation(float *x, float *y, int sz)
{
    CvMemStorage *storage = cvCreateMemStorage(0);
    CvSeq *pointSeq = cvCreateSeq(CV_32FC2, sizeof(CvSeq), 
                                  sizeof(CvPoint2D32f), storage);
    
    for (int i = 0; i < sz; i++)
    {
        CvPoint2D32f p = cvPoint2D32f(x[i], y[i]);
        cvSeqPush(pointSeq, &p);
    }
    
    float line[4];
    cvFitLine(pointSeq, CV_DIST_L2, 0, 0.01, 0.01, line);

    Line out;
    out.px = line[2];
    out.py = line[3];
    out.vx = line[0];
    out.vy = line[1];

    cvClearSeq(pointSeq);
    cvReleaseMemStorage(&storage);
    return out;
}


/*!\fn get_lines_intersection(Line line1, Line line2, float eps)
 * \param [in] line1 - first line.
 * \param [in] line2 - second line.
 * \param [in] eps - stability calculation parameter.
 * \return point of two lines intersection.
 *
 * Having two line structures return the point of the corresponding lines
 * intersection. If lines are quite close to be collinear, NULL pointer 
 * will be returned.
 */
CvPoint2D32f *get_lines_intersection(Line line1, Line line2, float eps)
{
    if (1 - fabs(line1.vx*line2.vx + line1.vy*line2.vy) < eps)
        return NULL;

    CvPoint2D32f *point = (CvPoint2D32f*)malloc(sizeof(CvPoint2D32f));

    CvMat *A = cvCreateMat(2, 2, CV_32FC1);
    CV_MAT_ELEM(*A, float, 0, 0) = line1.vx;
    CV_MAT_ELEM(*A, float, 0, 1) = line2.vx;
    CV_MAT_ELEM(*A, float, 1, 0) = line1.vy;
    CV_MAT_ELEM(*A, float, 1, 1) = line2.vy;

    CvMat *b = cvCreateMat(2, 1, CV_32FC1);
    CV_MAT_ELEM(*b, float, 0, 0) = line2.px - line1.px;
    CV_MAT_ELEM(*b, float, 1, 0) = line2.py - line1.py;

    CvMat *invA = cvCreateMat(2, 2, CV_32FC1);
    cvInvert(A, invA, CV_LU);

    CvMat *x = cvCreateMat(2, 1, CV_32FC1);
    cvMatMul(invA, b, x);

    point->x = CV_MAT_ELEM(*x, float, 0, 0)*line1.vx + line1.px;
    point->y = CV_MAT_ELEM(*x, float, 0, 0)*line1.vy + line1.py;

    cvReleaseMat(&A);
    cvReleaseMat(&invA);
    cvReleaseMat(&b);
    cvReleaseMat(&x);

    return point;
}
