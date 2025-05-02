#include "OniSampleUtilities.h"
#include "UVC_Swapper.h"
#include "UVCSwapper.h"
#include "OBTypes.h"
#include "ObCommon.h"

#include <fstream>

using namespace std;

//RGB w x h
#define IMAGE_WIDTH_640 640	
#define IMAGE_HEIGHT_480 480

//Read data outtime
#define UVC_TIME_OUT 3000 //ms

int main()
{
	UVC_Swapper uvsSwapper;
	uvsSwapper.UvcInit();

	bool mStart = false;
	
	UVCDeviceInfo* deviceInfo = uvsSwapper.getDeviceInfo();
	printf("UVC device vid= %d, pid = %d.\n", deviceInfo->deviceVid, deviceInfo->devicePid);
	int fps = 30;
	if (deviceInfo->devicePid == 0x052b)
	{
		fps = 25;
	}
	
	
	//OB_PIXEL_FORMAT_YUV422 or OB_PIXEL_FORMAT_MJPEG
	uvsSwapper.UVCStreamStart(IMAGE_WIDTH_640, IMAGE_HEIGHT_480, OB_PIXEL_FORMAT_MJPEG, fps);
	mStart = true;

	//Data buffer
	uint8_t* mUvcBuff = new uint8_t[IMAGE_WIDTH_640*IMAGE_HEIGHT_480 * 2];
	while (!wasKeyboardHit() && mStart)
	{
		uint32_t nSize = 0;
		uint32_t nImageType = 0;
		memset(mUvcBuff, 0, IMAGE_WIDTH_640*IMAGE_HEIGHT_480 * 2);
		int mRet = uvsSwapper.WaitUvcStream(mUvcBuff, nSize, nImageType, UVC_TIME_OUT);
		if (mRet != CAMERA_STATUS_SUCCESS)
		{
			return mRet;
		}
		else
		{
			printf("Receive image data , size = %d, format = %d.\n", nSize, nImageType);
		}

		/*std::ofstream outFile; 
		outFile.open("rgb.raw"); 
		int i = 0;
		for(i = 0; i < nSize; i++) 
		{ 
			outFile << mUvcBuff[i]; 
		} 
		outFile.close();*/

	}

	uvsSwapper.UVCStreamStop();
	uvsSwapper.UvcDeInit();

	if (mUvcBuff != NULL)
	{
		delete mUvcBuff;
		mUvcBuff = NULL;
	}

    return 0;
}
