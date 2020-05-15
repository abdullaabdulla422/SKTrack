#include "Context.h"

/*!\fn _point(int x, int y)
 * \param [in] x - x-coordinate.
 * \param [in] y - y-coordinate.
 * \return corresponding point.
 */
_Point _point(int x, int y)
{
    _Point p = {x, y};
    return p;
}


/*! \fn _size(int width, int height)
 * \param [in] width - the width.
 * \param [in] height - the height.
 * \return corresponding size.
 */
_Size _size(int width, int height)
{
    _Size sz = {width, height};
    return sz;
}

