#pragma once
#include <opencv2/opencv.hpp>
#include "OpenNI.h"
using namespace openni;

#define DEPTH_TIMEOUT 500
#define UVC_TIME_OUT 3000

//start astra device pid
#define AstraPID_0401 0x0401
#define AstraPID_0402 0x0402
#define AstraPID_0404 0x0404
#define AstraPID_MINI_PRO 0x065b

#define AstraPID_0407 0x0407
#define AstraPID_0408 0x0408
#define AstraPID_NH_GLST 0x0601
//end astra device pid

//start uvc device pid 
//640x480
#define ASTRAPRO_PID 0x0403
#define ButterFly_PID 0x0618
#define AstraProPlus_PID 0x060F
#define ASTRAPLUS_PID 0x0632
#define ASTRAPLUS_JIA_PID 0x0633

//400x640
#define ATLAS_PID 0x0613
//#define ASTRA_D2_PID 0x060d

//640x400
#define DEEYEA_PID 0x060b
#define Canglong_PID 0x0608
#define LunaP2_PID 0x0609
#define DaiBai_PID 0x060e
#define DaiBai_DC1 0x0657
#define DABAI_DCW_PID 0x0659
#define DABAI_GEMINI_E_PID 0x065c

#define BUS_CL_PID 0x0610	//bus not have rgb sensor
#define ASTRA_BUS_PID 0x0611 //bus not have rgb sensor
#define GEMINI_PID 0x0614
#define Projector_PID 0x0617

#define PetrelPro_PID 0x062b
#define PetrelPlus_PID 0x062c
#define PetrelB_PID 0x0634

//end uvc device pid

#define IMAGE_WIDTH_640 640
#define IMAGE_HEIGHT_480 480
#define IMAGE_HEIGHT_400 400
#define IMAGE_HEIGHT_360 360
#define IMAGE_HEIGHT_384 384

#define IMAGE_WIDTH_512 512
#define IMAGE_WIDTH_480 480
#define IMAGE_WIDTH_400 400
#define IMAGE_HEIGHT_640 640

typedef enum
{
	HARDWARE_D2C = 0,
	SOFTWARE_D2C = 1,
	NORMAL_NO = 2,
} ObD2CType;

class d2cSwapper
{
public:
	d2cSwapper();
	virtual ~d2cSwapper();

public:
	//�������ܣ������ʼ��
	virtual int CameraInit(int d2cType) = 0;

	//�������ܣ�����D2Cģʽ
	virtual int SetD2CType(int decType) = 0;

	//�������ܣ��������ʼ��
	virtual int CameraUnInit(void) = 0;

	//�������ܣ���ȡ���������
	virtual int GetStreamData(cv::Mat &cv_rgb, cv::Mat &cv_depth) = 0;

	//�������ܣ�ֹͣ��
	virtual int StreamStop(void) = 0;

	//��������: ��ȡ����������
	//������
	//[out] cameraParam: ����������
	//����ֵ��0:��ʾOK; ��0��ʾ��ȡ����ʧ��
	virtual int GetCameraParam(OBCameraParams &cameraParam) = 0;

	//�������ܣ���ȡDepth�ֱ���
	//������
	//����[Out] nImageWidth: ͼ���;
	//����[Out] nImageHeight: ͼ���;
	//����ֵ���ɹ����� CAMERA_STATUS_SUCCESS��ʧ�ܷ��� CAMERA_STATUS_DEPTH_GET_RESOLUTION_FAIL
	virtual int GetCameraResolution(int &nImageWidth, int &nImageHeight) = 0;

	virtual int GetSoftCameraResolution(int &nImageWidth, int &nImageHeight) = 0;

	//�������ܣ���ȡ�豸��pid
	virtual uint16_t GetDevicePid(void) = 0;
protected:
	int m_bD2cType;
};

