/*****************************************************************************
*                                                                            *
*  OpenNI 2.x Alpha                                                          *
*  Copyright (C) 2012 PrimeSense Ltd.                                        *
*                                                                            *
*  This file is part of OpenNI.                                              *
*                                                                            *
*  Licensed under the Apache License, Version 2.0 (the "License");           *
*  you may not use this file except in compliance with the License.          *
*  You may obtain a copy of the License at                                   *
*                                                                            *
*      http://www.apache.org/licenses/LICENSE-2.0                            *
*                                                                            *
*  Unless required by applicable law or agreed to in writing, software       *
*  distributed under the License is distributed on an "AS IS" BASIS,         *
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
*  See the License for the specific language governing permissions and       *
*  limitations under the License.                                            *
*                                                                            *
*****************************************************************************/
// Undeprecate CRT functions
#ifndef _CRT_SECURE_NO_DEPRECATE 
	#define _CRT_SECURE_NO_DEPRECATE 1
#endif

#include "Viewer.h"

#if (ONI_PLATFORM == ONI_PLATFORM_MACOSX)
        #include <GLUT/glut.h>
#else
        #include <GL/glut.h>
#endif

#include <PS1080.h>
#include "OniSampleUtilities.h"

#define GL_WIN_SIZE_X	1280
#define GL_WIN_SIZE_Y	1024
#define TEXTURE_SIZE	512

#define DEFAULT_DISPLAY_MODE	DISPLAY_MODE_DEPTH

#define MIN_NUM_CHUNKS(data_size, chunk_size)	((((data_size)-1) / (chunk_size) + 1))
#define MIN_CHUNKS_SIZE(data_size, chunk_size)	(MIN_NUM_CHUNKS(data_size, chunk_size) * (chunk_size))

static uint16_t s_OldProductPids[] =
{
	0x0401,
	0x0402,
	0x0403,
	0x0404,
	0x0405,
	0x0406,
	0x0407,
	0x0408,
	0x0409,
	0x040a,
	0x040b,
	0x040c,
	0x0632,
	0x0633,
	0x0617,
	0x0601,
	0x060f
};

SampleViewer* SampleViewer::ms_self = NULL;

char sn[32] = { 0 };
char type[32] = { 0 };
int gain = 0;
int exposure = 0;
int ldp_en = 0;
int laser_en = 1;
int ir_flood_en = 1;

void SampleViewer::glutIdle()
{
	glutPostRedisplay();
}
void SampleViewer::glutDisplay()
{
	SampleViewer::ms_self->display();
}
void SampleViewer::glutKeyboard(unsigned char key, int x, int y)
{
	SampleViewer::ms_self->onKey(key, x, y);
}

SampleViewer::SampleViewer(const char* strSampleName, openni::Device& device, openni::VideoStream& depth, openni::VideoStream& ir) :
m_device(device), m_depthStream(depth), m_irStream(ir), m_streams(NULL), m_eViewState(DEFAULT_DISPLAY_MODE), m_pTexMap(NULL), m_bOldVersion(false)

{
	ms_self = this;
	strncpy(m_strSampleName, strSampleName, ONI_MAX_STR);
	uint16_t nPid = m_device.getDeviceInfo().getUsbProductId();
	int nSize = sizeof(s_OldProductPids) / sizeof(s_OldProductPids[0]);
	for (int i = 0; i < nSize; ++i)
	{
		if (s_OldProductPids[i] == nPid)
		{
			m_bOldVersion = true;
			break;
		}
	}
}

SampleViewer::~SampleViewer()
{
	delete[] m_pTexMap;

	ms_self = NULL;

	if (m_streams != NULL)
	{
		delete []m_streams;
	}
}

openni::Status SampleViewer::init(int argc, char **argv)
{
	openni::VideoMode depthVideoMode;
	openni::VideoMode irVideoMode;

	if (m_depthStream.isValid() && m_irStream.isValid())
	{
		depthVideoMode = m_depthStream.getVideoMode();
		irVideoMode = m_irStream.getVideoMode();

		int depthWidth = depthVideoMode.getResolutionX();
		int depthHeight = depthVideoMode.getResolutionY();
		int irWidth = irVideoMode.getResolutionX();
		int irHeight = irVideoMode.getResolutionY();

		if (depthWidth <= irWidth )
		{
			m_width = depthWidth;
			m_height = depthHeight;
		}
		else
		{	
			m_width = irWidth;
		}

		if (depthHeight <= irHeight )
		{
			m_height = depthHeight;
		}
		else
		{	
			m_height = irHeight;
		}

/*
		if (depthWidth == irWidth &&
			depthHeight == irHeight)
		{
			m_width = depthWidth;
			m_height = depthHeight;
		}
		else
		{
			printf("Error - expect ir and depth to be in same resolution: D: %dx%d, C: %dx%d\n",
				depthWidth, depthHeight,
				irWidth, irHeight);
			return openni::STATUS_ERROR;
		}
*/
	}
	else if (m_depthStream.isValid())
	{
		depthVideoMode = m_depthStream.getVideoMode();
		m_width = depthVideoMode.getResolutionX();
		m_height = depthVideoMode.getResolutionY();
	}
	else if (m_irStream.isValid())
	{
		irVideoMode = m_irStream.getVideoMode();
		m_width = irVideoMode.getResolutionX();
		m_height = irVideoMode.getResolutionY();
	}
	else
	{
		printf("Error - expects at least one of the streams to be valid...\n");
		return openni::STATUS_ERROR;
	}

	m_streams = new openni::VideoStream*[2];
	m_streams[0] = &m_depthStream;
	m_streams[1] = &m_irStream;

	// Texture map init
	m_nTexMapX = MIN_CHUNKS_SIZE(m_width, TEXTURE_SIZE);
	m_nTexMapY = MIN_CHUNKS_SIZE(m_height, TEXTURE_SIZE);
	m_pTexMap = new openni::RGB888Pixel[m_nTexMapX * m_nTexMapY];

	return initOpenGL(argc, argv);

}
openni::Status SampleViewer::run()	//Does not return
{
	glutMainLoop();

	return openni::STATUS_OK;
}

void SampleViewer::display()
{
	int changedIndex;
	openni::Status rc = openni::OpenNI::waitForAnyStream(m_streams, 2, &changedIndex);
	if (rc != openni::STATUS_OK)
	{
		printf("Wait failed\n");
		return;
	}

	switch (changedIndex)
	{
	case 0:
		m_depthStream.readFrame(&m_depthFrame); break;
	case 1:
		m_irStream.readFrame(&m_irFrame); break;
	default:
		printf("Error in wait\n");
	}

	glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();
	glOrtho(0, GL_WIN_SIZE_X, GL_WIN_SIZE_Y, 0, -1.0, 1.0);

	if (m_depthFrame.isValid())
	{
		calculateHistogram(m_pDepthHist, MAX_DEPTH, m_depthFrame);
	}

	memset(m_pTexMap, 0, m_nTexMapX*m_nTexMapY*sizeof(openni::RGB888Pixel));

	// check if we need to draw image frame to texture
	if ((m_eViewState == DISPLAY_MODE_OVERLAY ||
		m_eViewState == DISPLAY_MODE_IR) && m_irFrame.isValid())
	{
		const openni::Grayscale16Pixel* pImageRow = (const openni::Grayscale16Pixel*)m_irFrame.getData();
		openni::RGB888Pixel* pTexRow = m_pTexMap + m_irFrame.getCropOriginY() * m_nTexMapX;
		int rowSize = m_irFrame.getStrideInBytes() / sizeof(openni::Grayscale16Pixel);

		for (int y = 0; y < m_irFrame.getHeight(); ++y)
		{
			const openni::Grayscale16Pixel* pImage = pImageRow;
			openni::RGB888Pixel* pTex = pTexRow + m_irFrame.getCropOriginX();

			for (int x = 0; x < m_irFrame.getWidth(); ++x, ++pImage, ++pTex)
			{
				uint8_t textureValue = 0;
				textureValue = (uint8_t)((*pImage) >> 2);

				pTex->r = textureValue;
				pTex->g = textureValue;
				pTex->b = textureValue;
			}

			pImageRow += rowSize;
			pTexRow += m_nTexMapX;
		}
	}

	// check if we need to draw depth frame to texture
	if ((m_eViewState == DISPLAY_MODE_OVERLAY ||
		m_eViewState == DISPLAY_MODE_DEPTH) && m_depthFrame.isValid())
	{
		const openni::DepthPixel* pDepthRow = (const openni::DepthPixel*)m_depthFrame.getData();
		openni::RGB888Pixel* pTexRow = m_pTexMap + m_depthFrame.getCropOriginY() * m_nTexMapX;
		int rowSize = m_depthFrame.getStrideInBytes() / sizeof(openni::DepthPixel);

		for (int y = 0; y < m_depthFrame.getHeight(); ++y)
		{
			const openni::DepthPixel* pDepth = pDepthRow;
			openni::RGB888Pixel* pTex = pTexRow + m_depthFrame.getCropOriginX();

			for (int x = 0; x < m_depthFrame.getWidth(); ++x, ++pDepth, ++pTex)
			{
				if (*pDepth != 0)
				{
					int nHistValue = m_pDepthHist[*pDepth];
					pTex->r = nHistValue;
					pTex->g = nHistValue;
					pTex->b = 0;
				}
			}

			pDepthRow += rowSize;
			pTexRow += m_nTexMapX;
		}
	}

	glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP_SGIS, GL_TRUE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, m_nTexMapX, m_nTexMapY, 0, GL_RGB, GL_UNSIGNED_BYTE, m_pTexMap);

	// Display the OpenGL texture map
	glColor4f(1,1,1,1);

	glBegin(GL_QUADS);

	int nXRes = m_width;
	int nYRes = m_height;

	// upper left
	glTexCoord2f(0, 0);
	glVertex2f(0, 0);
	// upper right
	glTexCoord2f((float)nXRes/(float)m_nTexMapX, 0);
	glVertex2f(GL_WIN_SIZE_X, 0);
	// bottom right
	glTexCoord2f((float)nXRes/(float)m_nTexMapX, (float)nYRes/(float)m_nTexMapY);
	glVertex2f(GL_WIN_SIZE_X, GL_WIN_SIZE_Y);
	// bottom left
	glTexCoord2f(0, (float)nYRes/(float)m_nTexMapY);
	glVertex2f(0, GL_WIN_SIZE_Y);

	glEnd();

	// Swap the OpenGL display buffers
	glutSwapBuffers();

}

void SampleViewer::onKey(unsigned char key, int /*x*/, int /*y*/)
{
	int size = 0;
	openni::Status rc;
	switch (key)
	{
	case 27:
		m_depthStream.stop();
		m_irStream.stop();
		m_depthStream.destroy();
		m_irStream.destroy();
		m_device.close();
		openni::OpenNI::shutdown();

		exit (1);
	case '1':
		m_eViewState = DISPLAY_MODE_IR;
		m_depthStream.stop();
		m_irStream.start();
		break;
	case '2':
		m_eViewState = DISPLAY_MODE_DEPTH;
		m_irStream.stop();
		m_depthStream.start();
		break;
	case '3':
		size = 12;
		m_device.getProperty(openni::OBEXTENSION_ID_SERIALNUMBER, sn, &size);
		printf("serial number : %s\n", sn);
		break;
	case '4':
		size = 32;
		m_device.getProperty(openni::OBEXTENSION_ID_DEVICETYPE, type, &size);
		printf("device type : %s\n", type);
		break;
	case '5':
		if (ir_flood_en == 0x00)
		{
			ir_flood_en = 0x01;
			m_device.setProperty(XN_MODULE_PROPERTY_IRFLOOD_STATE, 1);
			printf("turn on ir flood...\n");
		}
		else
		{
			ir_flood_en = 0x00;
			m_device.setProperty(XN_MODULE_PROPERTY_IRFLOOD_STATE, 0);
			printf("turn off ir flood...\n");
		}
		break;
	case '6':
		size = 4;
		m_device.getProperty(openni::OBEXTENSION_ID_IR_GAIN, (uint8_t*)&gain, &size);
		printf("ir gain value : 0x%x\n", gain);
		break;
	case '7':
		size = 4;
		m_device.getProperty(openni::OBEXTENSION_ID_IR_EXP, (uint8_t*)&exposure, &size);
		printf("ir exposure value : 0x%x\n", exposure);
		break;
	case '8':
		if (m_bOldVersion)
		{
			m_depthStream.stop();
			m_irStream.stop();
			size = 4;
			rc=m_device.getProperty(openni::OBEXTENSION_ID_LDP_EN, (uint8_t*)&ldp_en, &size);
			if (rc==openni::STATUS_OK)
			{
				if (ldp_en == 0x00)
				{
					ldp_en = 0x01;
					rc=m_device.setProperty(openni::OBEXTENSION_ID_LDP_EN, (uint8_t*)&ldp_en, 4);
					printf("turn on ldp ");
					if (rc == openni::STATUS_OK)
					{
						printf("success\n");
					}
					else
					{
						printf("fail\n");
					}
				}
				else if (ldp_en == 0x01)
				{
					ldp_en = 0x00;
					rc=m_device.setProperty(openni::OBEXTENSION_ID_LDP_EN, (uint8_t*)&ldp_en, 4);
					printf("turn off ldp ");
					if (rc == openni::STATUS_OK)
					{
						printf("success\n");
					}
					else
					{
						printf("fail\n");
					}
				}
			}
			else
			{
				printf("Error: %s\n", openni::OpenNI::getExtendedError());
			}
			m_depthStream.start();
		}
		else
		{
			size = 4;
			rc=m_device.getProperty(XN_MODULE_PROPERTY_LDP_ENABLE, (uint8_t*)&ldp_en, &size);
			if (rc == openni::STATUS_OK)
			{
				if (ldp_en == 0x00)
				{
					ldp_en = 0x01;
					rc=m_device.setProperty(XN_MODULE_PROPERTY_LDP_ENABLE, (uint8_t*)&ldp_en, 4);
					printf("turn on ldp ");
					if (rc == openni::STATUS_OK)
					{
						printf("success\n");
					}
					else
					{
						printf("fail\n");
					}
				}
				else if (ldp_en == 0x01)
				{
					ldp_en = 0x00;
					rc=m_device.setProperty(XN_MODULE_PROPERTY_LDP_ENABLE, (uint8_t*)&ldp_en, 4);
					printf("turn off ldp ");
					if (rc == openni::STATUS_OK)
					{
						printf("success\n");
					}
					else
					{
						printf("fail\n");
					}
				}
			}
			else
			{
				printf("Error: %s\n", openni::OpenNI::getExtendedError());
			}
			
		}
		break;
	case '9':
		if (laser_en == 0x00)
		{
			laser_en = 0x01;
			m_device.setProperty(openni::OBEXTENSION_ID_LASER_EN, (uint8_t*)&laser_en, 4);
			printf("turn on laser...\n");
		}
		else if (laser_en == 0x01)
		{
			laser_en = 0x00;
			m_device.setProperty(openni::OBEXTENSION_ID_LASER_EN, (uint8_t*)&laser_en, 4);
			printf("turn off laser...\n");
		}
		break;
	case '0':
		m_depthStream.setMirroringEnabled(!m_depthStream.getMirroringEnabled());
		m_irStream.setMirroringEnabled(!m_irStream.getMirroringEnabled());
		break;
	case 'g':
		size = 4;
		m_device.getProperty(openni::OBEXTENSION_ID_IR_GAIN, (uint8_t *)&gain, &size);
		printf("ir gain value : 0x%x\n", gain);
		gain++;

		size = 4;
		m_device.setProperty(openni::OBEXTENSION_ID_IR_GAIN, (uint8_t *)&gain, size);
		break;
	case 'G':
		size = 4;
		m_device.getProperty(openni::OBEXTENSION_ID_IR_GAIN, (uint8_t *)&gain, &size);
		printf("ir gain value : 0x%x\n", gain);
		gain--;

		size = 4;
		m_device.setProperty(openni::OBEXTENSION_ID_IR_GAIN, (uint8_t *)&gain, size);
		break;
	case 'e':
		size = 4;
		m_device.getProperty(openni::OBEXTENSION_ID_IR_EXP, (uint8_t *)&exposure, &size);
		printf("ir exposure value : 0x%x\n", exposure);
		exposure += 256;

		size = 4;
		m_device.setProperty(openni::OBEXTENSION_ID_IR_EXP, (uint8_t *)&exposure, size);
		break;
	case 'E':
		size = 4;
		m_device.getProperty(openni::OBEXTENSION_ID_IR_EXP, (uint8_t *)&exposure, &size);
		printf("ir exposure value : 0x%x\n", exposure);
		exposure -= 256;

		size = 4;
		m_device.setProperty(openni::OBEXTENSION_ID_IR_EXP, (uint8_t *)&exposure, size);
		break;
	case 'r':
		size = 4;
		gain = 0x8;
		m_device.setProperty(openni::OBEXTENSION_ID_IR_GAIN, (uint8_t *)&gain, size);
		break;
	case 'R':
		size = 4;
		exposure = 0x419;
		m_device.setProperty(openni::OBEXTENSION_ID_IR_EXP, (uint8_t *)&exposure, size);
		break;
	}

}

openni::Status SampleViewer::initOpenGL(int argc, char **argv)
{
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH);
	glutInitWindowSize(GL_WIN_SIZE_X, GL_WIN_SIZE_Y);
	glutCreateWindow (m_strSampleName);
	// 	glutFullScreen();
	glutSetCursor(GLUT_CURSOR_NONE);

	initOpenGLHooks();

	glDisable(GL_DEPTH_TEST);
	glEnable(GL_TEXTURE_2D);

	return openni::STATUS_OK;

}
void SampleViewer::initOpenGLHooks()
{
	glutKeyboardFunc(glutKeyboard);
	glutDisplayFunc(glutDisplay);
	glutIdleFunc(glutIdle);
}

