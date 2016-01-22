#pragma version(1)
#pragma rs java_package_name(com.github.krukon.tutoratamicamera)
#pragma rs_fp_relaxed

rs_allocation in;
rs_allocation out;
rs_script script;

int imageWidth;
int imageHeight;

const uchar4 *inPixels;
uchar4 *outPixels;

const int matrixSize = 3;
const float kernel[] = {
		-1, -1, -1,
		-1, 8, -1,
		-1, -1, -1
};

void root(const int32_t *v_in, int32_t  *v_out, const void *usrData, uint32_t x, uint32_t y) {
	for(int x = 0; x < imageWidth; x++){
		float4 output;
		output.r = output.g = output.b = 0;
		int position[] = {-1, 0, 1};

		for(int i = 0; i < matrixSize; ++i){
		   for(int j = 0; j < matrixSize; ++j){
				float4 neighbourPixel = rsUnpackColor8888(inPixels[x + *v_in + position[i] + position[j]]);
				output.r += (neighbourPixel.r * kernel[i * matrixSize + j]);
				output.g += (neighbourPixel.g * kernel[i * matrixSize + j]);
				output.b += (neighbourPixel.b * kernel[i * matrixSize + j]);
		   }
		}

		if (output.r > 1.0) output.r = 1.0f;
		if (output.g > 1.0) output.g = 1.0f;
		if (output.b > 1.0) output.b = 1.0f;

		outPixels[x + *v_in] = rsPackColorTo8888(output.rgb);
	}
}

void filter() {
    rsForEach(script, in, out);
}
