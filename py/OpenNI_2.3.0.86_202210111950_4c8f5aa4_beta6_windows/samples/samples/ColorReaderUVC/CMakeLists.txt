set(TARGET ColorReaderUVC)

project(${TARGET})

include_directories(
	../ThirdParty/Common
	../ThirdParty/OpenNI2/Include
    #../ThirdParty/OpenCV241/Include
    ../ThirdParty/UvcSwapper/Include
)

if(CMAKE_SYSTEM_NAME MATCHES "Windows")
	MESSAGE(STATUS "IS Windows platform")
	if(CMAKE_SIZEOF_VOID_P EQUAL 8)
		MESSAGE(STATUS "IS Windows x64")
		# 64 bits
		link_directories(
			../ThirdParty/OpenNI2/windows/x64-Release
			#../ThirdParty/OpenCV241/x64/Lib
			../ThirdParty/UvcSwapper/x64-Release
		)
	elseif(CMAKE_SIZEOF_VOID_P EQUAL 4)
		MESSAGE(STATUS "IS Windows x86")
		# 32 bits
		link_directories(
			../ThirdParty/OpenNI2/windows/Win32-Release
			#../ThirdParty/OpenCV241/x86/Lib
			../ThirdParty/UvcSwapper/x86-Release
		)
	endif()

elseif(CMAKE_SYSTEM_NAME MATCHES "Linux")
	add_definitions(-DPLATFORM_LINUX)
	MESSAGE(STATUS "IS Linux platform")
	set(CMAKE_BUILD_WITH_INSTALL_RPATH TRUE)
	set(CMAKE_INSTALL_RPATH .)
	if(BUILD_PLATFORM MATCHES "x86_64")
		if(CMAKE_SIZEOF_VOID_P EQUAL 8)
			MESSAGE(STATUS "IS Linux x64")
			# 64 bits
			link_directories(
			 	../ThirdParty/OpenNI2/linux/x64
			 	../ThirdParty/UvcSwapper/x64
			)


		elseif(CMAKE_SIZEOF_VOID_P EQUAL 4)
			MESSAGE(STATUS "IS Linux x86")
			# 32 bits
			link_directories(
				../ThirdParty/OpenNI2/linux/x86
				../ThirdParty/UvcSwapper/x86
			
			)
		endif()
	elseif(BUILD_PLATFORM MATCHES "armv7l")
			MESSAGE(STATUS "IS armv7l")
			# 32 bits
			link_directories(
				../ThirdParty/OpenNI2/arm/Arm
				../ThirdParty/UvcSwapper/arm-32
			)
	elseif(BUILD_PLATFORM MATCHES "aarch64")
			MESSAGE(STATUS "ColorReaderUVC is aarch64")
			link_directories(
				../ThirdParty/OpenNI2/arm/Arm64
				../ThirdParty/UvcSwapper/arm-64
			)
	endif()

else()
	
endif()

file(GLOB SRC "*.h" "*.cpp")

add_executable(${TARGET} ${SRC})

target_link_libraries(${TARGET} Uvc-Swapper OpenNI2)

