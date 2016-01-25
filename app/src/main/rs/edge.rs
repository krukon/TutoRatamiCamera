#pragma version(1)
#pragma rs java_package_name(com.github.krukon.tutoratamicamera)
#pragma rs_fp_relaxed

rs_allocation in;
rs_allocation out;
rs_script script;

int imageWidth;
int imageHeight;

const int deltaX[] = {0, 1, 1, 1, 0, -1, -1, -1};
const int deltaY[] = {1, 1, 0, -1, -1, -1, 0, 1};

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 output;
	output.r = output.g = output.b = 0;

	float weight = 8;
	for (int i = 0; i < 8; ++i) {
		if (x + deltaX[i] < 0 || x + deltaX[i] >= imageWidth ||
			y + deltaY[i] < 0 || y + deltaY[i] >= imageHeight) {
			weight--;
			continue;
		}
        float4 neighbourPixel = rsUnpackColor8888(rsGetElementAt_uchar4(in, x + deltaX[i], y + deltaY[i]));

		output.r -= neighbourPixel.r;
		output.g -= neighbourPixel.g;
		output.b -= neighbourPixel.b;
	}

	float4 pixel = rsUnpackColor8888(*v_in);
	output.r += weight * pixel.r;
	output.g += weight * pixel.g;
	output.b += weight * pixel.b;

	output.r = output.g = output.b = (output.r + output.g + output.b) / 3.0;

	*v_out = rsPackColorTo8888(output.rgb);
}

void filter() {
    rsForEach(script, in, out);
}
