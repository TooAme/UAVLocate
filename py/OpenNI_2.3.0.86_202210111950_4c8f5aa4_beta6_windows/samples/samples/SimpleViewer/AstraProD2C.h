#pragma once
#include "d2cSwapper.h"
#include "UVC_Swapper.h"

class AstroProD2C :public d2cSwapper
{
public:
	AstroProD2C();
	virtual ~AstroProD2C();

	//�������ܣ������ʼ��
	int CameraInit(int d2cType);

	//�������ܣ�����D2C����
	int SetD2CType(int d2cType);

	//�������ܣ��������ʼ��
	int CameraUnInit(void);

	//�������ܣ���ȡ���������
	int GetStreamData(cv::Mat &cv_rgb, cv::Mat &cv_depth);

	//�������ܣ�ֹͣ��
	int StreamStop(void);

	//��������: ��ȡ����������
	//������
	//[out] cameraParam: ����������
	//����ֵ��0:��ʾOK; ��0��ʾ��ȡ����ʧ��
	int GetCameraParam(OBCameraParams &cameraParam);

	//�������ܣ���ȡDepth�ֱ���
	//������
	//����[Out] nImageWidth: ͼ���;
	//����[Out] nImageHeight: ͼ���;
	//����ֵ���ɹ����� CAMERA_STATUS_SUCCESS��ʧ�ܷ��� CAMERA_STATUS_DEPTH_GET_RESOLUTION_FAIL
	int GetCameraResolution(int &nImageWidth, int &nImageHeight);
	int GetSoftCameraResolution(int &nImageWidth, int &nImageHeight);

	//�������ܣ���ȡ�豸��pid
	uint16_t GetDevicePid(void);


private:

	/**** start depth swapper ****/
	int DepthInit(void);
	int DepthUnInit();

	int Depthstart(int width, int height);
	int Depthstop();
	int WaitDepthStream(VideoFrameRef &frame);
	void CalcDepthHist(VideoFrameRef& frame);

	//depth data
	Device m_device;

	VideoStream m_depthStream;
	openni::VideoFrameRef m_depthFrame;
	bool m_bDepthInit;
	bool m_bDepthStart;

	bool m_bDepStreamCreate;
	float* m_histogram;
	int m_ImageWidth;
	int m_ImageHeight;

	/***end depth swapper********/

private:

	/*****start uvc swapper******************/
	UVC_Swapper m_UvcSwapper;

	//D2C Ŀǰֻ֧��640x480�Ķ���
	uint8_t *m_pUvcBuff;

	bool m_bUvcInit;
	bool m_bUvcStart;
	/*****end uvc swapper********************/
};

