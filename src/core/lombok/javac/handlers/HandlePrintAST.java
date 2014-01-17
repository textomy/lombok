/*
 * Copyright (C) 2009-2014 The Project Lombok Authors.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.javac.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.mangosdk.spi.ProviderFor;

import com.sun.tools.javac.tree.JCTree.JCAnnotation;

import lombok.Lombok;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.PrintAST;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;

/**
 * Handles the {@code lombok.core.PrintAST} annotation for javac.
 */
@ProviderFor(JavacAnnotationHandler.class)
@HandlerPriority(536870912) // 2^29; this handler is customarily run at the very end.
public class HandlePrintAST extends JavacAnnotationHandler<PrintAST> {
	@Override public void handle(AnnotationValues<PrintAST> annotation, JCAnnotation ast, JavacNode annotationNode) {
		PrintStream stream = System.out;
		String fileName = annotation.getInstance().outfile();
		if (fileName.length() > 0) try {
			stream = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			Lombok.sneakyThrow(e);
		}
		
		try {
			annotationNode.up().traverse(new JavacASTVisitor.Printer(annotation.getInstance().printContent(), stream));
		} finally {
			if (stream != System.out) {
				try {
					stream.close();
				} catch (Exception e) {
					Lombok.sneakyThrow(e);
				}
			}
		}
	}
}
