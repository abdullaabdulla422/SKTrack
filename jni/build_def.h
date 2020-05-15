/*!\file build_def.h
 * \brief build modes definitions.
 */
#ifndef BUILD_DEF_H
#define BUILD_DEF_H

/*!\def FULL_CV_DEBUG
* \brief Comment/uncomment it if you want to build project with/without full computer vision functions debug.
 */
//#define FULL_CV_DEBUG

/*!\def CALC_DIAGNOSTIC_INFO
 * \brief Comment/uncomment it if you want to build project with/without diagnostic info calculation.
 */
#define CALC_DIAGNOSTIC_INFO

//#define SHOW_FINAL_DIAGNOSTIC

#if defined FULL_CV_DEBUG && defined CALC_DIAGNOSTIC_INFO && !defined SHOW_FINAL_DIAGNOSTIC
#define SHOW_FINAL_DIAGNOSTIC
#endif

#endif
