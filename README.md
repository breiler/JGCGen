Java G-code generator
======================

JGCGen is a preprocessor and code generator for NC files used to control
CNC milling machines. It is built around Apache Velocity template engine.
JGCGen aims to be an easy to use program which stays out of the way when
not needed, but still have enough advanced features to make your work
easier.

The code generated by jgcgen is relatively human readable, parametrized
and makes use of looping constructs for conciseness.

What can JGCgen do for you? It will
 * Keep track of O word numbers
 * Expand macros
 * Composite code from multiple files
 * Generate toolpaths for geometric shapes like rectangles, circles, etc.
 * Manipulate tool paths
 * Render text (Hershey fonts)
 * Split a single source file into multiple output files

The code generation functions are currently optimized for 3 axis mills,
but jgcgen can be used to code for any type of machine.

Installation
-------------

Jgcgen can be compiled using Eclipse or from the command line with `ant`.
The following dependencies are needed:
 * Velocity engine + dependencies (http://velocity.apache.org/)
 * Commons-cli (http://commons.apache.org/cli/)

Place the velocity and commons-cli jars in the jars/ subdirectory, then
run `ant` or export a runnable jar with Eclipse.

### Using with EMC ###

You can have EMC automatically call jgcgen when opening a .jgc file.
Simply add these lines to your machine configuration ini file:

	PROGRAM_EXTENSION = .jgc JGCGen script
	jgc = java -jar /path/to/your/jgcgen.jar -o -
	

Examples
---------

### Subroutines and flow control ###

Jgcgen takes the drudgery out of O words.

	#o("sub", "cut_square")
		#3 = -0.5
		g00 x[#1-10] y[#2+10] z#<_safe>
		#o("repeat", "10")
			g01 z#3
				x[#1+10]
				y[#2-10]
				x[#1-10]
				y[#2+10]
			#3 = [#3 - 0.5]
		#end
		g00 z#<_safe>
	#end

	#ocall("cut_square", "0", "0")

The above subroutine cuts a little square centered around the given X and Y coordinates.


### Ready made paths ###

	#o("sub", "cut_square")
		#g($Shapes.outline.rectangle.corners("[#1-10]", "[#2-10]", "20", "20"), "-5") 
	#end

The above subroutine cuts a little square centered around the given X and Y coordinates.
Note that you can use g-code variables when defining the rectangle coordinates!
The number of cutting passes made depends on the g-code variable `#<_passdepth>.`


### Path capture ###

If a built-in method can't generate a suitable path for you, you can write it yourself.

	#capture($path)
	g00 x-10 y0
	g01 x10
	g02 x-10 i-10
	#end
	
	#g($path.offset("x10 y10"), "-5")

The above code cuts a little D shape 5 millimeters deep. (Top of the workpiece is assumed to be at Z0)
No Z coordinates need to be given. Safe Z (`#<_zsafe>`) is automatically added to rapids and
the cut will be made in passes `#<_passdepth>` deep until the offset (-5) is reached.

### Output splitting ###

You can split jgcgen output into multiple files by running it with the `-s`
parameter. This is useful if you want the program separated into multiple
independent parts, e.g. for manual tool changes.

	This part goes to all files.
	
	#split(0)
	This part is omitted if splitting is enabled.
	#end
	
	#split(1)
	This part goes to file_1.ngc
	If splitting is to be used, split block 1 must be present!
	#end
	
	#split(100)
	The blocks don't need to be consecutive. 
	#end
	
	#split(2)
	This part goes to file_2.ngc
	#end

### Text engraving ###

Jgcgen comes bundled with a set of engraving fonts (Hershey fonts)
The sample below engraves the text "Hello World" with 2 mm letter spacing
using a times roman font.
	
	#set($font = $Fonts.get("timesr.jhf"))
	$font.setOption("lspace", 2.0)

	#g3($font.getString("Hello world!", "-1")
