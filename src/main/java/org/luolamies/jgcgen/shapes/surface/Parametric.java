package org.luolamies.jgcgen.shapes.surface;

import org.apache.velocity.exception.ParseErrorException;
import org.luolamies.jgcgen.RenderException;
import org.luolamies.jgcgen.shapes.Shapes;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.SymbolTable;

/**
 * A parametric surface
 *
 * @author Calle Laakkonen
 */
public class Parametric extends Surface {
	private final JEP jep;
	private final Shapes shapes;
	private double resolution = 0.1;
	private double zmin=0, zscale=1.0, maxz=1.0;
	private String z0, z1;
	private double xoff, yoff;
	private Node jepnode;
	
	public Parametric(Shapes shapes) {
		this.shapes = shapes;
		jep = new JEP();
		jep.addStandardFunctions();
		jep.addStandardConstants();
		jep.setAllowUndeclared(true);
	}
	
	/**
	 * Define the function describing the surface.
	 * The function should return values in range [z0,z1]. ZScale should be used to scale to the desired height.
	 * @param func
	 * @return this
	 */
	public Parametric f(String func, String z0, String z1) {
		this.z0 = z0;
		this.z1 = z1;

		jep.parseExpression(func);
		jepnode = jep.getTopNode();
		if(jep.hasError())
			throw new ParseErrorException(jep.getErrorInfo());

		return this;
	}
	
	
	/**
	 * Define the function describing the surface.
	 * The function should return values in range [0,1]. ZScale should be used to scale to the desired height.
	 * @param func
	 * @return this
	 */
	public Parametric f(String func) {
		return f(func, "0", "1");
	}
	
	/**
	 * Set the surface resolution. This affects path generation.
	 * @param res
	 * @return
	 */
	public Parametric resolution(double res) {
		if(res<=0)
			throw new IllegalArgumentException("Resolution must be greater than zero!");
		this.resolution = res;
		return this;
	}

	public double getAspectRatio() {
		return 1;
	}

	public double getDepthAt(double x, double y) {
		jep.addVariable("x", x+xoff);
		jep.addVariable("y", y+yoff);
        return -(jep.getValue() - zmin) * zscale;
	}

	public double getMaxZ() {
		return maxz;
	}

	public double getResolution() {
		return resolution;
	}

	public void setTargetSize(double width, double height, double depth) {
		xoff = -width / 2;
		yoff = height / 2;

		// Set dimension constants
        jep.addVariable("w", width);
		jep.addVariable("h", height);
		jep.addVariable("d", depth);
		
		// Evaluate Z scaling expressions
		jep.parseExpression(z0);
		this.zmin = jep.getValue();

        jep.parseExpression(z1);
        double z1 = jep.getValue();

		// Set velocity variables
		SymbolTable symbols = jep.getSymbolTable();
		for(Object key : symbols.keySet()) {
			if(!"x".equals(key) && !"y".equals(key)) {
                throw new RuntimeException("Code not migrated to use new JEP library yet");
				/*Variable var  = symbols.getVar((String)key);
				if(!var.isConstant()) {
					Object val = shapes.ctx.get(var.getName());
					if(val==null || !(val instanceof Number))
						throw new ParseErrorException("Variable \"" + var.getName() + "\" is not a number!");
					var.setValue(val);
				}*/
			}
		}
		
		if(z1==this.zmin)
			throw new RenderException("Range cannot be zero!");
		
		this.maxz = depth;
		this.zscale = depth / (z1 - zmin);
	}

}
