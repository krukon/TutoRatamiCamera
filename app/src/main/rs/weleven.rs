#pragma version(1)
#pragma rs java_package_name(com.github.krukon.tutoratamicamera)
#pragma rs_fp_relaxed

int imageWidth;
int imageHeight;

int left, top, right, bottom;

rs_allocation in;
rs_allocation out;
rs_script script;

int copyInput;
const uchar4 mono2 = {0, 0, 0, 0};

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    if (copyInput) {
        *v_out = *v_in;
        return;
    }

    if (left <= x && x <= right && top <= y && y <= bottom) {
        *v_out = mono2;
    }
}

void filter() {
    rsForEach(script, in, out);

}