#pragma version(1)
#pragma rs java_package_name(com.github.krukon.tutoratamicamera)
#pragma rs_fp_relaxed

int imageWidth;
int imageHeight;

rs_allocation in;
rs_allocation out;
rs_script script;

const float DEPTH = 0.45;
const float scale = DEPTH / (1.0 - DEPTH);

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    float4 p = rsUnpackColor8888(*v_in);

    float dx = (1.0 + scale) * (x - imageWidth * 0.5) / (imageWidth * 0.5);
    float dy = (1.0 + scale) * (y - imageHeight * 0.5) / (imageHeight * 0.5);

    dx *= dx; dy *= dy;
    dx *= dx; dy *= dy;
    dx *= dx; dy *= dy;
    float d = 1 + (1 - sqrt(sqrt(sqrt(dx + dy)))) / scale;

    p.r *= d;
    p.g *= d;
    p.b *= d;

    *v_out = rsPackColorTo8888(p);
}

void filter() {
    rsForEach(script, in, out);
}
