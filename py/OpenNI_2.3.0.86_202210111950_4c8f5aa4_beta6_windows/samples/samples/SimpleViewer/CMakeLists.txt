set(TARGET SimpleViewer)

project(${TARGET})

include_directories(
    ../ThirdParty/d2c/Include
    ../ThirdParty/OpenNI2/Include
    ../ThirdParty/UvcSwapper/Include
)


if(CMAKE_SYSTEM_NAME MATCHES "Windows")
	include_directories(
		../ThirdParty/OpenCV420/Windows/include
	)
	
	add_definitions(-DPLATFORM_WIN)
	add_definitions(-DSW_WIN)
	MESSAGE(STATUS "IS Windows platform")
	
	if(CMAKE_SIZEOF_VOID_P EQUAL 8)
		MESSAGE(STATUS "IS Windows x64")
		# 64 bits
		link_directories(
			../ThirdParty/OpenNI2/windows/x64-Release
			../ThirdParty/OpenCV420/Windows/lib/win_x64
			../ThirdParty/d2c/lib/win64/release
			../ThirdParty/UvcSwapper/x64-Release
		)
	elseif(CMAKE_SIZEOF_VOID_P EQUAL 4)
		MESSAGE(STATUS "IS Windows x86")
		# 32 bits
		link_directories(
			../ThirdParty/OpenNI2/windows/Win32-Release
			../ThirdParty/OpenCV420/Windows/lib/win_x86
			../ThirdParty/d2c/lib/win32/release
			../ThirdParty/UvcSwapper/x86-Release
		)
	endif()

elseif(CMAKE_SYSTEM_NAME MATCHES "Linux")
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -g -fPIC -O3 -std=c++11 -fopenmp")
	include_directories(
		../ThirdParty/OpenCV420/Linux/include/opencv4
	)
	add_definitions(-DPLATFORM_LINUX)
	add_definitions(-DSW_LINUX)
	MESSAGE(STATUS "SimpleViewer is Linux platform")
	set(CMAKE_BUILD_WITH_INSTALL_RPATH TRUE)
	set(CMAKE_INSTALL_RPATH .)
	if(BUILD_PLATFORM MATCHES "x86_64")
		if(CMAKE_SIZEOF_VOID_P EQUAL 8)
			MESSAGE(STATUS "IS Linux x64")
			# 64 bits
			link_directories(
			../ThirdParty/OpenNI2/linux/x64
			../ThirdParty/OpenCV420/Linux/lib
			../ThirdParty/d2c/lib/x64
			../ThirdParty/UvcSwapper/x64
		)
		elseif(CMAKE_SIZEOF_VOID_P EQUAL 4)
			MESSAGE(STATUS "IS Linux x86")
			# 32 bits
			link_directories(
				../ThirdParty/OpenNI2/linux/x86
			)
		endif()

	elseif(BUILD_PLATFORM MATCHES "armv7l")
		MESSAGE(STATUS "IS armv7l")
		# 32 bits
		link_directories(
			../ThirdParty/OpenNI2/arm/Arm
			../ThirdParty/OpenCV420/arm-32/lib
			../ThirdParty/d2c/lib/arm-32
			../ThirdParty/UvcSwapper/arm-32
		)
	elseif(BUILD_PLATFORM MATCHES "aarch64")
		MESSAGE(STATUS "IS aarch64")
		link_directories(
			../ThirdParty/OpenNI2/arm/Arm64
			../ThirdParty/OpenCV420/arm-64/lib
			../ThirdParty/d2c/lib/arm-64
			../ThirdParty/UvcSwapper/arm-64
		)
	endif()

else()
	
endif()



file(GLOB SRC "*.h" "*.cpp")

add_executable(${TARGET} ${SRC})

if(CMAKE_SYSTEM_NAME MATCHES "Windows")
	target_link_libraries(${TARGET} Uvc-Swapper OpenNI2 opencv_world430 d2c)
elseif(CMAKE_SYSTEM_NAME MATCHES "Linux")
	target_link_libraries(${TARGET} Uvc-Swapper OpenNI2 opencv_core opencv_highgui opencv_imgcodecs opencv_imgproc d2c)
else()
	
endif()
