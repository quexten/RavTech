varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float time;
const float PI = 3.14159;

void main()
{	
	float alpha = texture2D(u_texture, v_texCoords).a;
	gl_FragColor = vec4(alpha, alpha, alpha, 1.0);
}