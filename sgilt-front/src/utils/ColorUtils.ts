export const colorLuminance = (hex: string , lum: number): string => {

	// validate hex string
	hex = String(hex).replace(/[^0-9a-f]/gi, '');
	if (hex.length < 6) {
		hex = hex[0]+hex[0]+hex[1]+hex[1]+hex[2]+hex[2];
	}
	lum = lum || 0;

	// convert to decimal and change luminosity
	let rgb = "#", c, i;
	for (i = 0; i < 3; i++) {
		c = parseInt(hex.substring(i*2, i*2 + 2), 16);
		c = Math.round(Math.min(Math.max(0, c + (c * lum)), 255)).toString(16);
		rgb += ("00"+c).slice(-2);
	}

	return rgb;
}
