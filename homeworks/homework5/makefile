
all: ex1 ex2 ex3 ex4 ex5

ex1: ex1.md
	pandoc -s --mathjax ex1.md -o ex1.pdf

ex2: ex2.md
	pandoc -s --mathjax ex2.md -o ex2.pdf

ex3: ex3.md
	pandoc -s --mathjax ex3.md -o ex3.pdf

ex4: ex4.md
	pandoc -s --mathjax ex4.md -o ex4.pdf

ex5: ex5.md
	pandoc -s --mathjax ex5.md -o ex5.pdf

rmAll:
	rm ex1.pdf
	rm ex2.pdf
	rm ex3.pdf
	rm ex4.pdf
	rm ex5.pdf

homework: all
	gs -dBATCH -dNOPAUSE -q -sDEVICE=pdfwrite -sOutputFile=IFT6561_Homework5_GabrielCParent.pdf frontPage.pdf ex1.pdf ex2.pdf ex3.pdf ex4.pdf ex5.pdf
	make rmAll
