set(TARGET MultiDepthViewer)

project(${TARGET})

include_directories(
    ../ThirdParty/Common
    ../ThirdParty/OpenNI2/Include
    ../ThirdParty/GL
)

if(CMAKE_SYSTEM_NAME MATCHES "Windows")
	MESSAGE(STATUS "IS Windows platform")
	if(CMAKE_SIZEOF_VOID_P EQUAL 8)
		MESSAGE(STATUS "IS Windows x64")
		# 64 bits
		link_directories(
			../ThirdParty/OpenNI2/windows/x64-Release
			../ThirdParty/GL
		)
	elseif(CMAKE_SIZEOF_VOID_P EQUAL 4)
		MESSAGE(STATUS "IS Windows x86")
		# 32 bits
		link_directories(
			../ThirdParty/OpenNI2/windows/Win32-Release
			../ThirdParty/GL
		)
	endif()

	set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} /SAFESEH:NO")
	set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} /SAFESEH:NO")
	set(CMAKE_MODULE_LINKER_FLAGS "${CMAKE_MODULE_LINKER_FLAGS} /SAFESEH:NO")

elseif(CMAKE_SYSTEM_NAME MATCHES "Linux")
	MESSAGE(STATUS "IS Linux platform")
	set(CMAKE_BUILD_WITH_INSTALL_RPATH TRUE)
	set(CMAKE_INSTALL_RPATH .)
	if(BUILD_PLATFORM MATCHES "x86_64")
		if(CMAKE_SIZEOF_VOID_P EQUAL 8)
			MESSAGE(STATUS "IS Linux x64")
			# 64 bits
			link_directories(
				../ThirdParty/OpenNI2/linux/x64
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
		)
	elseif(BUILD_PLATFORM MATCHES "aarch64")
		MESSAGE(STATUS "IS aarch64")
		link_directories(
			../ThirdParty/OpenNI2/arm/Arm64
		)
	endif()

else()
	
endif()

set(SRC_FILES main.cpp Viewer.cpp)
add_executable(${TARGET} ${SRC_FILES})
#add_executable(${TARGET} main.cpp Viewer.cpp Viewer.h)


#For Windows
if(CMAKE_SYSTEM_NAME MATCHES "Windows")
	if(CMAKE_SIZEOF_VOID_P EQUAL 8)
    		target_link_libraries(${TARGET} glut64 OpenNI2)
	elseif(CMAKE_SIZEOF_VOID_P EQUAL 4)
    		target_link_libraries(${TARGET} glut32 OpenNI2)
	endif()

else()
	target_link_libraries(${TARGET} OpenNI2 glut GL)

endif()


