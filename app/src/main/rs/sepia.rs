#pragma version(1)
#pragma rs java_package_name(com.github.krukon.tutoratamicamera)
#pragma rs_fp_relaxed

int imageWidth;
int imageHeight;

rs_allocation in;
rs_allocation out;
rs_script script;

float GS_RED = 0.3f;
float GS_GREEN = 0.59f;
float GS_BLUE = 0.11f;

float DEPTH = 0.25f;

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    float4 f4 = rsUnpackColor8888(*v_in);
    float val = GS_RED * f4.r + GS_GREEN * f4.g + GS_BLUE * f4.b;
    f4.r = f4.g = f4.b = val;

    f4.r +=  4 * DEPTH * f4.r;
    f4.g += 2 * DEPTH * f4.g;

    if (f4.r > 1.0) f4.r = 1.0f;
    if (f4.g > 1.0) f4.g = 1.0f;
    if (f4.b > 1.0) f4.b = 1.0f;
    if (f4.b < 0.0) f4.b = 0.0f;

    float3 mono = {f4.r, f4.g, f4.b};

    *v_out = rsPackColorTo8888(mono);
}

void filter() {
    rsForEach(script, in, out);

}