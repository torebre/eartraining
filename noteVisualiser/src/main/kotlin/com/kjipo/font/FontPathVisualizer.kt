package com.kjipo.font

import javafx.scene.Group
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.slf4j.LoggerFactory
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths


class FontPathVisualizer : View("Glyph view") {

    private val LOGGER = LoggerFactory.getLogger(FontPathVisualizer::class.java)

    override val root = stackpane {
//        val lineSegments = Files.newInputStream(Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg/gonvillepart1.svg")).use { ReadFonts.extractGlyphFromFile("clefs.G", it) }
//        val lineSegments = Files.newInputStream(Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg/gonvillepart1.svg")).use { ReadFonts.extractGlyphFromFile("rests.M2", it) }

        val lineSegments = processPath(ReadFonts.parsePathData("M359.403 1080.91c-7.621 -2.80005 -37.638 -31.6801 -54.954 -52.88c-56.862 -69.564 -87.028 -156.64 -87.028 -247.587c0 -23.399 1.99699 -47.054 6.049 -70.732c1.037 -6.22101 16.486 -79.528 19.701 -93.577l8.81299 -40.957l7.98399 -37.586l-22.137 -22.293 c-63.405 -63.715 -115.248 -124.735 -166.573 -195.967c-47.6846 -66.184 -71.2582 -141.493 -71.2582 -215.793c0 -86.4519 31.9152 -171.537 94.8988 -239.288c63.7742 -68.645 157.378 -107.88 248.941 -107.88c24.405 0 48.665 2.78801 72.176 8.548 c4.41602 1.08 8.13501 1.927 8.565 1.927c0.0249939 0 0.0379944 -0.00300598 0.0410156 -0.00900269c0.156006 -0.207001 20.323 -94.251 22.863 -106.849c5.362 -26.344 8.04303 -50.971 8.04303 -73.803c0 -25.739 -3.40701 -49.198 -10.22 -70.269 c-21.962 -67.944 -89.714 -113.801 -162.345 -113.801c-9.66702 0 -19.421 0.812012 -29.164 2.49298c-15.916 2.74799 -35.513 8.91699 -46.556 14.724l-2.43599 1.24402l4.50999 0.052002c51.947 0.258972 94.251 43.704 94.251 96.843v0.0639954 c0 60.903 -49.755 100.629 -102.135 100.629c-25.878 0 -52.397 -9.69598 -73.873 -31.327c-21.275 -21.412 -32.0448 -48.407 -32.0448 -75.406c0 -23.857 8.40881 -47.717 25.4088 -67.733c42.01 -49.483 102.251 -75.143 162.599 -75.143 c45.999 0 92.061 14.909 130.16 45.54c50.448 40.604 75.529 97.486 75.529 171.686c0 13.339 -0.809998 27.237 -2.42999 41.702c-2.073 18.871 -4.66501 32.505 -17.574 92.747c-6.68802 31.417 -12.184 57.287 -12.184 57.495c0 0.259003 2.64401 1.55501 5.85901 2.903 c96.07 41.129 160.99 135.723 160.99 233.392c0 2.42 -0.039978 4.8418 -0.120972 7.2647c-4.09503 124.321 -91.399 221.008 -213.387 236.199c-3.77698 0.468994 -12.737 0.694 -22.131 0.694c-11.362 0 -23.358 -0.32901 -27.586 -0.95401 c-2.88602 -0.411987 -5.36801 -0.742981 -5.94202 -0.742981c-0.0749817 0 -0.117981 0.00500488 -0.123993 0.0180054c-0.052002 0.0509949 -8.39899 39.193 -18.612 86.941l-18.56 86.837l9.28 8.96899l24.833 23.796c93.318 88.601 139.77 181.659 147.028 294.626 c0.414001 6.45001 0.618011 13.029 0.618011 19.713c0 95.296 -41.3 211.963 -100.209 279.061c-15.417 17.5801 -25.878 25.3601 -34.587 25.3601c-1.71597 0 -3.36499 -0.300049 -4.96997 -0.890015zM405.025 938.029c15.294 -2.02197 28.203 -10.576 34.062 -22.448 c5.57098 -11.3 8.97501 -33.439 8.97501 -54.775c0 -6.85602 -0.351013 -13.6281 -1.095 -19.932c-8.96902 -76.313 -53.658 -162.114 -127.068 -243.871c-6.95499 -7.77899 -30.547 -32.042 -31.149 -32.042l-0.00900269 0.00299072 c-0.415009 0.415039 -21.567 100.784 -22.707 107.679c-2.871 17.671 -4.28699 35.201 -4.28699 52.382c0 54.194 14.092 104.918 41.095 145.659c26.619 40.244 66.071 67.745 96.091 67.745c2.07898 0 4.112 -0.132019 6.09198 -0.400024zM298.85 355.725 c5.237 -24.574 9.79898 -46.089 10.214 -47.8c1.651 -7.172 12.546 -58.512 12.546 -59.045v-0.0039978c-0.103973 -0.052002 -1.452 -0.519012 -3.00699 -1.037c-85.973 -28.644 -140.686 -106.965 -140.686 -190.863c0 -19.0575 2.823 -38.4027 8.74399 -57.5188 c16.02 -51.7915 54.799 -94.9251 104.257 -116.025c3.89499 -1.667 7.51199 -2.476 10.718 -2.476c7.16602 0 12.28 4.043 13.856 11.6c0.260986 1.269 0.395996 2.461 0.395996 3.596c0 5.6344 -3.34201 9.8285 -11.283 14.7051 c-37.122 22.7868 -55.619 61.0151 -55.619 100.107c0 39.7976 19.171 80.4903 57.382 106.696c10.145 6.94099 34.561 18.519 38.779 18.519c0.286987 0 0.480988 -0.0540009 0.570007 -0.166c0.102997 -0.104004 6.065 -27.84 13.272 -61.642l14.101 -66.1003 l1.763 -8.03572l8.76102 -40.9562l21.567 -100.784c7.414 -34.786 13.479 -63.508 13.479 -63.819c0 -1.45099 -22.552 -7.465 -36.29 -9.746c-12.705 -2.103 -25.384 -3.127 -37.946 -3.127c-98.609 0 -189.955 63.113 -229.825 162.856 c-13.514 33.8666 -20.178 70.0703 -20.178 106.672c0 56.0726 15.64 113.081 46.255 164.054c15.502 25.766 55.836 79.165 93.37 123.646c18.197 21.567 53.917 61.434 55.006 61.434c0.156006 0 4.56201 -20.115 9.798 -44.74zM403.781 140.938 c86.237 -9.468 149.006 -85.2966 149.006 -169.824c0 -13.5982 -1.62396 -27.4215 -5.03699 -41.23c-10.524 -42.6156 -38.053 -81.9126 -76.261 -108.768c-6.37802 -4.519 -20.001 -12.832 -20.993 -12.832c-0.0320129 0 -0.0509949 0.00900269 -0.0559998 0.0270081 c-0.154999 0.466995 -6.89499 31.832 -8.96899 41.578c-0.777008 3.83699 -15.138 70.9739 -31.831 149.102c-16.694 78.1797 -30.381 142.258 -30.381 142.413c0 0.378998 2.392 0.552994 5.90298 0.552994c5.138 0 12.675 -0.371994 18.619 -1.019z"))
//        val lineSegments = processPath(ReadFonts.parsePathData("M359.403 1080.91c-7.621 -2.80005 -37.638 -31.6801 -54.954 -52.88c-56.862 -69.564 -87.028 -156.64 -87.028 -247.587c0 -23.399 1.99699 -47.054 6.049 -70.732c1.037 -6.22101 16.486 -79.528 19.701 -93.577l8.81299 -40.957l7.98399 -37.586l-22.137 -22.293 c-63.405 -63.715 -115.248 -124.735 -166.573 -195.967c-47.6846 -66.184 -71.2582 -141.493 -71.2582 -215.793c0 -86.4519 31.9152 -171.537 94.8988 -239.288c63.7742 -68.645 157.378 -107.88 248.941 -107.88c24.405 0 48.665 2.78801 72.176 8.548 c4.41602 1.08 8.13501 1.927 8.565 1.927c0.0249939 0 0.0379944 -0.00300598 0.0410156 -0.00900269c0.156006 -0.207001 20.323 -94.251 22.863 -106.849 c5.362 -26.344 8.04303 -50.971 8.04303 -73.803c0 -25.739 -3.40701 -49.198 -10.22 -70.269 c-21.962 -67.944 -89.714 -113.801 -162.345 -113.801c-9.66702 0 -19.421 0.812012 -29.164 2.49298c-15.916 2.74799 -35.513 8.91699 -46.556 14.724l-2.43599 1.24402l4.50999 0.052002c51.947 0.258972 94.251 43.704 94.251 96.843v0.0639954 c0 60.903 -49.755 100.629 -102.135 100.629c-25.878 0 -52.397 -9.69598 -73.873 -31.327c-21.275 -21.412 -32.0448 -48.407 -32.0448 -75.406c0 -23.857 8.40881 -47.717 25.4088 -67.733c42.01 -49.483 102.251 -75.143 162.599 -75.143 c45.999 0 92.061 14.909 130.16 45.54c50.448 40.604 75.529 97.486 75.529 171.686c0 13.339 -0.809998 27.237 -2.42999 41.702c-2.073 18.871 -4.66501 32.505 -17.574 92.747c-6.68802 31.417 -12.184 57.287 -12.184 57.495c0 0.259003 2.64401 1.55501 5.85901 2.903 c96.07 41.129 160.99 135.723 160.99 233.392c0 2.42 -0.039978 4.8418 -0.120972 7.2647c-4.09503 124.321 -91.399 221.008 -213.387 236.199c-3.77698 0.468994 -12.737 0.694 -22.131 0.694c-11.362 0 -23.358 -0.32901 -27.586 -0.95401 c-2.88602 -0.411987 -5.36801 -0.742981 -5.94202 -0.742981c-0.0749817 0 -0.117981 0.00500488 -0.123993 0.0180054c-0.052002 0.0509949 -8.39899 39.193 -18.612 86.941l-18.56 86.837l9.28 8.96899l24.833 23.796c93.318 88.601 139.77 181.659 147.028 294.626 c0.414001 6.45001 0.618011 13.029 0.618011 19.713c0 95.296 -41.3 211.963 -100.209 279.061c-15.417 17.5801 -25.878 25.3601 -34.587 25.3601c-1.71597 0 -3.36499 -0.300049 -4.96997 -0.890015zM405.025 938.029c15.294 -2.02197 28.203 -10.576 34.062 -22.448 c5.57098 -11.3 8.97501 -33.439 8.97501 -54.775c0 -6.85602 -0.351013 -13.6281 -1.095 -19.932c-8.96902 -76.313 -53.658 -162.114 -127.068 -243.871c-6.95499 -7.77899 -30.547 -32.042 -31.149 -32.042l-0.00900269 0.00299072 c-0.415009 0.415039 -21.567 100.784 -22.707 107.679c-2.871 17.671 -4.28699 35.201 -4.28699 52.382c0 54.194 14.092 104.918 41.095 145.659c26.619 40.244 66.071 67.745 96.091 67.745c2.07898 0 4.112 -0.132019 6.09198 -0.400024z"))


        group {
//            svgpath("M359.403 1080.91c-7.621 -2.80005 -37.638 -31.6801 -54.954 -52.88c-56.862 -69.564 -87.028 -156.64 -87.028 -247.587c0 -23.399 1.99699 -47.054 6.049 -70.732c1.037 -6.22101 16.486 -79.528 19.701 -93.577l8.81299 -40.957l7.98399 -37.586l-22.137 -22.293 c-63.405 -63.715 -115.248 -124.735 -166.573 -195.967c-47.6846 -66.184 -71.2582 -141.493 -71.2582 -215.793c0 -86.4519 31.9152 -171.537 94.8988 -239.288c63.7742 -68.645 157.378 -107.88 248.941 -107.88c24.405 0 48.665 2.78801 72.176 8.548 c4.41602 1.08 8.13501 1.927 8.565 1.927c0.0249939 0 0.0379944 -0.00300598 0.0410156 -0.00900269c0.156006 -0.207001 20.323 -94.251 22.863 -106.849 c5.362 -26.344 8.04303 -50.971 8.04303 -73.803c0 -25.739 -3.40701 -49.198 -10.22 -70.269 c-21.962 -67.944 -89.714 -113.801 -162.345 -113.801c-9.66702 0 -19.421 0.812012 -29.164 2.49298c-15.916 2.74799 -35.513 8.91699 -46.556 14.724l-2.43599 1.24402l4.50999 0.052002c51.947 0.258972 94.251 43.704 94.251 96.843v0.0639954 c0 60.903 -49.755 100.629 -102.135 100.629c-25.878 0 -52.397 -9.69598 -73.873 -31.327c-21.275 -21.412 -32.0448 -48.407 -32.0448 -75.406c0 -23.857 8.40881 -47.717 25.4088 -67.733c42.01 -49.483 102.251 -75.143 162.599 -75.143 c45.999 0 92.061 14.909 130.16 45.54c50.448 40.604 75.529 97.486 75.529 171.686c0 13.339 -0.809998 27.237 -2.42999 41.702c-2.073 18.871 -4.66501 32.505 -17.574 92.747c-6.68802 31.417 -12.184 57.287 -12.184 57.495c0 0.259003 2.64401 1.55501 5.85901 2.903 c96.07 41.129 160.99 135.723 160.99 233.392c0 2.42 -0.039978 4.8418 -0.120972 7.2647c-4.09503 124.321 -91.399 221.008 -213.387 236.199c-3.77698 0.468994 -12.737 0.694 -22.131 0.694c-11.362 0 -23.358 -0.32901 -27.586 -0.95401 c-2.88602 -0.411987 -5.36801 -0.742981 -5.94202 -0.742981c-0.0749817 0 -0.117981 0.00500488 -0.123993 0.0180054c-0.052002 0.0509949 -8.39899 39.193 -18.612 86.941l-18.56 86.837l9.28 8.96899l24.833 23.796c93.318 88.601 139.77 181.659 147.028 294.626 c0.414001 6.45001 0.618011 13.029 0.618011 19.713c0 95.296 -41.3 211.963 -100.209 279.061c-15.417 17.5801 -25.878 25.3601 -34.587 25.3601c-1.71597 0 -3.36499 -0.300049 -4.96997 -0.890015zM405.025 938.029c15.294 -2.02197 28.203 -10.576 34.062 -22.448 c5.57098 -11.3 8.97501 -33.439 8.97501 -54.775c0 -6.85602 -0.351013 -13.6281 -1.095 -19.932c-8.96902 -76.313 -53.658 -162.114 -127.068 -243.871c-6.95499 -7.77899 -30.547 -32.042 -31.149 -32.042l-0.00900269 0.00299072 c-0.415009 0.415039 -21.567 100.784 -22.707 107.679c-2.871 17.671 -4.28699 35.201 -4.28699 52.382c0 54.194 14.092 104.918 41.095 145.659c26.619 40.244 66.071 67.745 96.091 67.745c2.07898 0 4.112 -0.132019 6.09198 -0.400024z") {
//
////            svgpath("M359.403 1080.91c-7.621 -2.80005 -37.638 -31.6801 -54.954 -52.88c-56.862 -69.564 -87.028 -156.64 -87.028 -247.587c0 -23.399 1.99699 -47.054 6.049 -70.732c1.037 -6.22101 16.486 -79.528 19.701 -93.577l8.81299 -40.957l7.98399 -37.586l-22.137 -22.293 c-63.405 -63.715 -115.248 -124.735 -166.573 -195.967c-47.6846 -66.184 -71.2582 -141.493 -71.2582 -215.793c0 -86.4519 31.9152 -171.537 94.8988 -239.288c63.7742 -68.645 157.378 -107.88 248.941 -107.88c24.405 0 48.665 2.78801 72.176 8.548 c4.41602 1.08 8.13501 1.927 8.565 1.927c0.0249939 0 0.0379944 -0.00300598 0.0410156 -0.00900269c0.156006 -0.207001 20.323 -94.251 22.863 -106.849c5.362 -26.344 8.04303 -50.971 8.04303 -73.803c0 -25.739 -3.40701 -49.198 -10.22 -70.269 c-21.962 -67.944 -89.714 -113.801 -162.345 -113.801c-9.66702 0 -19.421 0.812012 -29.164 2.49298c-15.916 2.74799 -35.513 8.91699 -46.556 14.724l-2.43599 1.24402l4.50999 0.052002c51.947 0.258972 94.251 43.704 94.251 96.843v0.0639954 c0 60.903 -49.755 100.629 -102.135 100.629c-25.878 0 -52.397 -9.69598 -73.873 -31.327c-21.275 -21.412 -32.0448 -48.407 -32.0448 -75.406c0 -23.857 8.40881 -47.717 25.4088 -67.733c42.01 -49.483 102.251 -75.143 162.599 -75.143 c45.999 0 92.061 14.909 130.16 45.54c50.448 40.604 75.529 97.486 75.529 171.686c0 13.339 -0.809998 27.237 -2.42999 41.702c-2.073 18.871 -4.66501 32.505 -17.574 92.747c-6.68802 31.417 -12.184 57.287 -12.184 57.495c0 0.259003 2.64401 1.55501 5.85901 2.903 c96.07 41.129 160.99 135.723 160.99 233.392c0 2.42 -0.039978 4.8418 -0.120972 7.2647c-4.09503 124.321 -91.399 221.008 -213.387 236.199c-3.77698 0.468994 -12.737 0.694 -22.131 0.694c-11.362 0 -23.358 -0.32901 -27.586 -0.95401 c-2.88602 -0.411987 -5.36801 -0.742981 -5.94202 -0.742981c-0.0749817 0 -0.117981 0.00500488 -0.123993 0.0180054c-0.052002 0.0509949 -8.39899 39.193 -18.612 86.941l-18.56 86.837l9.28 8.96899l24.833 23.796c93.318 88.601 139.77 181.659 147.028 294.626 c0.414001 6.45001 0.618011 13.029 0.618011 19.713c0 95.296 -41.3 211.963 -100.209 279.061c-15.417 17.5801 -25.878 25.3601 -34.587 25.3601c-1.71597 0 -3.36499 -0.300049 -4.96997 -0.890015zM405.025 938.029c15.294 -2.02197 28.203 -10.576 34.062 -22.448 c5.57098 -11.3 8.97501 -33.439 8.97501 -54.775c0 -6.85602 -0.351013 -13.6281 -1.095 -19.932c-8.96902 -76.313 -53.658 -162.114 -127.068 -243.871c-6.95499 -7.77899 -30.547 -32.042 -31.149 -32.042l-0.00900269 0.00299072 c-0.415009 0.415039 -21.567 100.784 -22.707 107.679c-2.871 17.671 -4.28699 35.201 -4.28699 52.382c0 54.194 14.092 104.918 41.095 145.659c26.619 40.244 66.071 67.745 96.091 67.745c2.07898 0 4.112 -0.132019 6.09198 -0.400024zM298.85 355.725 c5.237 -24.574 9.79898 -46.089 10.214 -47.8c1.651 -7.172 12.546 -58.512 12.546 -59.045v-0.0039978c-0.103973 -0.052002 -1.452 -0.519012 -3.00699 -1.037c-85.973 -28.644 -140.686 -106.965 -140.686 -190.863c0 -19.0575 2.823 -38.4027 8.74399 -57.5188 c16.02 -51.7915 54.799 -94.9251 104.257 -116.025c3.89499 -1.667 7.51199 -2.476 10.718 -2.476c7.16602 0 12.28 4.043 13.856 11.6c0.260986 1.269 0.395996 2.461 0.395996 3.596c0 5.6344 -3.34201 9.8285 -11.283 14.7051 c-37.122 22.7868 -55.619 61.0151 -55.619 100.107c0 39.7976 19.171 80.4903 57.382 106.696c10.145 6.94099 34.561 18.519 38.779 18.519c0.286987 0 0.480988 -0.0540009 0.570007 -0.166c0.102997 -0.104004 6.065 -27.84 13.272 -61.642l14.101 -66.1003 l1.763 -8.03572l8.76102 -40.9562l21.567 -100.784c7.414 -34.786 13.479 -63.508 13.479 -63.819c0 -1.45099 -22.552 -7.465 -36.29 -9.746c-12.705 -2.103 -25.384 -3.127 -37.946 -3.127c-98.609 0 -189.955 63.113 -229.825 162.856 c-13.514 33.8666 -20.178 70.0703 -20.178 106.672c0 56.0726 15.64 113.081 46.255 164.054c15.502 25.766 55.836 79.165 93.37 123.646c18.197 21.567 53.917 61.434 55.006 61.434c0.156006 0 4.56201 -20.115 9.798 -44.74zM403.781 140.938 c86.237 -9.468 149.006 -85.2966 149.006 -169.824c0 -13.5982 -1.62396 -27.4215 -5.03699 -41.23c-10.524 -42.6156 -38.053 -81.9126 -76.261 -108.768c-6.37802 -4.519 -20.001 -12.832 -20.993 -12.832c-0.0320129 0 -0.0509949 0.00900269 -0.0559998 0.0270081 c-0.154999 0.466995 -6.89499 31.832 -8.96899 41.578c-0.777008 3.83699 -15.138 70.9739 -31.831 149.102c-16.694 78.1797 -30.381 142.258 -30.381 142.413c0 0.378998 2.392 0.552994 5.90298 0.552994c5.138 0 12.675 -0.371994 18.619 -1.019z") {
//                stroke = Color.RED
//                strokeWidth = 1.0
//                fill = Color.TRANSPARENT
//            }

            var previousPoint: CoordinatePair? = null
            var i = 0

            for (lineSegment in lineSegments) {

//                i++
//                if(i < 50 || i > 100)
//                    continue

                if (previousPoint != null && !lineSegment.skipLine) {

//                    LOGGER.info("Drawing line: " +previousPoint +", " +lineSegment);

                    circle {
                        centerX = previousPoint?.x ?: 0.0
                        centerY = previousPoint?.y ?: 0.0
                        radius = 2.0

                        stroke = Color.YELLOW
                        strokeWidth = 1.0
                    }

                    line {
                        startX = previousPoint?.x ?: 0.0
                        startY = previousPoint?.y ?: 0.0
                        endX = lineSegment.x
                        endY = lineSegment.y

                        stroke = Color.BLUE
                        strokeWidth = 1.0
                    }
                }
                previousPoint = lineSegment

            }
        }
    }


}