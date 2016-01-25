#pragma version(1)
#pragma rs java_package_name(com.github.krukon.tutoratamicamera)
#pragma rs_fp_relaxed

rs_allocation in;
rs_allocation out;
rs_script script;

int imageWidth;
int imageHeight;

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	*v_out = rsGetElementAt_uchar4(in, x, imageHeight - y - 1);
}

void filter() {
    rsForEach(script, in, out);
}
