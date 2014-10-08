precision mediump float;           // Set the default precision to medium. We don't need as high of a 
                                // precision in the fragment shader.
uniform sampler2D u_Texture;      // The input texture.
uniform float u_use_texture ;  // 设置是否使用纹理
uniform float u_alpha ;        // 设置整体透明度

varying vec3 v_Position;        // Interpolated position for this fragment.
varying vec4 v_Color;              // This is the color from the vertex shader interpolated across the 
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
  
// The entry point for our fragment shader.
void main()                            
{            gl_FragColor = v_Color * texture2D(u_Texture, v_TexCoordinate);
	if (u_use_texture < 0.5) {
	 	gl_FragColor = v_Color;
	} else {
		gl_FragColor = v_Color * texture2D(u_Texture, v_TexCoordinate);
	}
	
	if (u_alpha < 1) {
		gl_FragColor.r *= u_alpha;
		gl_FragColor.g *= u_alpha;
		gl_FragColor.b *= u_alpha;
		gl_FragColor.a *= u_alpha;
	}               
}