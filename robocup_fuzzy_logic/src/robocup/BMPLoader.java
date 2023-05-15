package robocup;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ReadableByteChannel;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class BMPLoader {

	static int loadBMP(String filename, int w, int h, boolean wrap) throws IOException {
		int width = w;
		int height = h;
		    
		IntBuffer texture = BufferUtils.createIntBuffer(1);
	    ByteBuffer data;

	    // create a direct ByteBuffer; see also Creating a ByteBuffer
	    ReadableByteChannel channel = new FileInputStream(filename).getChannel();
	    data = ByteBuffer.allocateDirect(width * height * 3 + 54); // 54 = BMP Header

	    channel.read(data);
	    data.rewind();
	    
	    // Ignoring BMP Header (54 bits);
	    data.position(54);
	    
	    // allocate a texture name
	    GL11.glGenTextures(texture);

	    // select our current texture
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.get(0));

	    // select modulate to mix texture with color for shading
	    GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, 
	    		GL11.GL_DECAL);

	    // when texture area is small, bilinear filter the closest mipmap
	    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
	    		GL11.GL_LINEAR_MIPMAP_NEAREST);
	    // when texture area is large, bilinear filter the first mipmap
	    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
	    		GL11.GL_LINEAR);

	    // if wrap is true, the texture wraps over at the edges (repeat)
	    //       ... false, the texture ends at the edges (clamp)
	    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 
	    		wrap ? GL11.GL_REPEAT : GL11.GL_CLAMP);
	    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 
	    		wrap ? GL11.GL_REPEAT : GL11.GL_CLAMP);

	    // build our texture mipmaps
	    GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, 3, width, height,
	    		GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, data);

	    // reseting Binding
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	    
	    return texture.get(0);
	}
}
