package org.herban;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Stack;
import java.util.Vector;

import android.hardware.Camera.Size;

public class RingBuffer {
	private Vector<ByteBuffer> imageList;
    private int time=0;
	public RingBuffer() {
		imageList = new Stack<ByteBuffer>();
	}

	public void add(byte[] imageBytes, Size previewSize) {

		imageList.add(ByteBuffer.wrap(imageBytes));
		if (imageList.size() > 5) {
			imageList.remove(0);
		}

	}

	public void writeTo(FileOutputStream out)  {
		int i = 0;
		for (ByteBuffer buf : imageList) {
			try {
				FileOutputStream fos = new FileOutputStream("/sdcard/a" + i
						+ "test"+time++ +".png");

				i++;
				fos.write(buf.array());
				fos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
