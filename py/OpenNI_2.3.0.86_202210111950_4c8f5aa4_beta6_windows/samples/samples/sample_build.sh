##linux的安装包
#build code 
#!/bin/bash

echo "p1="$1
platform_type="$1"


mkdir build
cd build
sudo rm -rf *
cmake .. -DBUILD_PLATFORM=$platform_type


if [[ "$platform_type" == "x86_64" ]]; then 

    	echo " Copy libuvc library for linux x86_64"
	cp -a ../ThirdParty/libuvc/x64/* ColorReaderUVC
	cp -a ../ThirdParty/libuvc/x64/* SimpleViewer

fi



if [[ "$platform_type" == "aarch64" ]]; then 
    	echo " Copy libuvc library for aarch64"
	cp -a ../ThirdParty/libuvc/arm-64/* ColorReaderUVC
	cp -a ../ThirdParty/libuvc/arm-64/* SimpleViewer
fi


if [[ "$platform_type" == "armv7l" ]]; then 
    	echo " Copy libuvc library for armv7l"
	cp -a ../ThirdParty/libuvc/arm-32/* ColorReaderUVC
	cp -a ../ThirdParty/libuvc/arm-32/* SimpleViewer
	cp -a ../ThirdParty/OpenCV420/arm-32/lib/* SimpleViewer
fi

make
sudo make install
