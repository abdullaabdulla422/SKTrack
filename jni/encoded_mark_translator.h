/*!\file encoded_mark_translator.h
 * \brief contains high-level procedures for encoded mark translator.
 */

#ifndef ENCODED_MARK_TRANSLATOR_H
#define ENCODED_MARK_TRANSLATOR_H

#include "image_processing.h"
#include "data_processing.h"
#include "Blob.h"
#include "diagnostic.h"
#include "build_def.h"


/*
#ifndef WIN32
#ifdef __cplusplus
extern "C"
{
#endif
#endif
*/

//! Initialization of mark finder.
void *mark_finder_init(int width, int height);
/*
#ifdef WIN32
	#ifdef DLLBUILD
extern "C"
{
__declspec(dllexport) void *MarkFinder_Init(int width, int height);
}
	#else
void *MarkFinder_Init(int width, int height);
	#endif
#else
void *MarkFinder_Init(int width, int height);
#endif
*/


//! Release mark finder.
void mark_finder_free(void *context);
/*
#ifdef WIN32
	#ifdef DLLBUILD
extern "C"
{
 __declspec(dllexport) void MarkFinder_Free(void *context);
}
	#else
void MarkFinder_Free(void *context);
	#endif
#else
void MarkFinder_Free(void *context);
#endif
*/

//! Translate having context and bitmap.
void *mark_finder_translate(void *context, CvMat *bitmap, bool enhance);
/*
#ifdef WIN32
	#ifdef DLLBUILD
extern "C"
{
 __declspec(dllexport) void *MarkFinder_Translate(void *context, unsigned char *bitmap);
}
	#else
void *MarkFinder_Translate(void *context, unsigned char *bitmap);
	#endif
#else
void *MarkFinder_Translate(void *context, unsigned char *bitmap);
#endif
*/

#ifndef DLLBUILD
//! Fill mark finder.
void fill_mark_finder(void *context, CvMat *bitmap, bool diagn, bool lightOnDark);
#endif

/*
#ifndef WIN32
#ifdef __cplusplus
}
#endif
#endif
*/

#endif
