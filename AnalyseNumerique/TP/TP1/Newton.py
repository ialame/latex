from tkinter import *
import numpy as np
# coordonnees initiales
x0, y0 = 10, 200
# vitesse initiale: angle de tir $\alpha$ et vitesse $v_0$
alpha=np.pi/3 ; V0=50
# pas du temps
h=0.1
z=np.array([x0,y0,V0*np.cos(alpha),-V0*np.sin(alpha)])
def f(z):
    return np.array([z[2],z[3],0,9.81])
X=x0+15;Y=y0+15
def Euler():
    global z,X,Y
    #z=z+h*f(z)
    z = z + h / 2 * (f(z) + f(z + h * f(z)))
    can1.create_line(X,Y,z[0]+15,z[1]+15)
    X=z[0]+15;Y= z[1]+15
    if z[0]<0 or z[0]>W-30:
        z[2]=-z[2]
    if z[1] < 0 or z[1] > H - 30:
        z[3] = -z[3]
    # deplacement de la balle a la nouvelle position
    can1.coords(balle, z[0], z[1], z[0] + 30, z[1] + 30)
    # La fenetre fen1 est actualisee en executant la
    # fonction Euler toutes les 10 millisecondes
    fen1.after(50, Euler)

# ========== Programme principal =============
# Creation de la fenetre principale :
fen1 = Tk()
fen1.title("Probleme de tir")
# creation du canvas :
H=W=750
can1 = Canvas(fen1, bg="dark grey", height=H, width=W)
can1.pack()
# creation de la balle
balle = can1.create_oval(x0, y0, x0 + 30, y0 + 30, width=2, fill="red")
# Lancement de la fonction Euler
Euler()
# demarrage de la boucle principale:
fen1.mainloop()