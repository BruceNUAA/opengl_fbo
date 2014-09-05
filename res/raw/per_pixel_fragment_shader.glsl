precision mediump float;           // Set the default precision to medium. We don't need as high of a 
                                // precision in the fragment shader.
uniform sampler2D u_Texture;    // The input texture.
  
varying vec3 v_Position;        // Interpolated position for this fragment.
varying vec4 v_Color;              // This is the color from the vertex shader interpolated across the 
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
  
// The entry point for our fragment shader.
void main()                            
{       
   vec3 v1 = vec3(v_Position.x, 0, v_Position.z);
   vec3 v2 = vec3(v_Position.x, 100, v_Position.z);
   bvec3 s1= lessThan(v1, v_Position);
   bvec3 s2= greaterThan(v2, v_Position);
  
    // Multiply the color by the diffuse illumination level and texture value to get final output color.
    if (any(s1) && any(s2)) {
    gl_FragColor = vec4(0, 0, 0, 0);
    } else {
    
    gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoordinate));    
    }                                      
}