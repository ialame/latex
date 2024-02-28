from tkinter import *
import numpy as np
H=W=750
# coordonnées initiales
x0,y0=W/2,600
# vitesse initiale
V0=100
h=0.05
z=np.array([y0,-V0])
# équadiff: y'=f(t,y)
def f(y):
    return np.array([z[1],9.81])
def Euler():
    global z
    z = z + h * f(z)
    # position de la balle
    can1.coords(balle,x0,z[0],x0+30,z[0]+30)
    fen1.after(10,Euler)

#========= Programme principal ============
fen1 = Tk()
fen1.title("Chute libre")
can1 =Canvas(fen1, bg='dark grey',height=H,width=W)
can1.pack()
balle = can1.create_oval(x0,y0,x0+30,y0+30,width=2,fill='red')
Euler()
fen1.mainloop()

