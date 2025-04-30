from openni import openni2
import numpy as np
import cv2


if __name__ == "__main__": 
    openni2.initialize()
    dev = openni2.Device.open_any()
    print(dev.get_device_info())

    depth_stream = dev.create_depth_stream()
    depth_stream.start()

    cap = cv2.VideoCapture(0)
    
    while True:
        dframe = depth_stream.read_frame()
        dframe = dframe.get_buffer_as_uint16()
        dframe = np.frombuffer(dframe, dtype=np.uint16).reshape((480, 640))
        cv2.imshow('depth', dframe)

        ret, frame = cap.read()
        cv2.imshow('color', frame)

        key = cv2.waitKey(1)
        if int(key) == ord('q'):
            break

    depth_stream.stop()
    dev.close()
