from tkinter import *
import numpy as np

def f(z):
    r = np.sqrt((z[0] - a) ** 2 + (z[1] - b) ** 2)
    A = G * Ms / r ** 2
    Ax, Ay = -A * (z[0] - a) / r, -A * (z[1] - b) / r
    return np.array([z[2], z[3], Ax , Ay ])

def Euler():
    global z, h
    z=z+h*f(z)
    x1,y1 = z[0] // k, z[1] // k
    can1.coords(terre, x1, y1, x1 + 20, y1 + 20)
    fen1.after(1, Euler)

# ========== Programme principal =============
H, W = 4.0E11, 4.0E11
a, b = W / 2, H / 2  # centre de la terre
k = 4.0E8
# coordonnees initiales
x0, y0 = a + 1.47E11, H / 2
# vitesse initiale
V0 = 30200
alpha = np.pi / 2

Ms = 1.989E30
G = 6.67E-11

z = np.array([x0, y0, V0 * np.cos(alpha), -V0 * np.sin(alpha)])
# 'pas' du temps
h =  3600

fen1 = Tk()
fen1.title("Terre-Soleil")

can1 = Canvas(fen1, bg='dark grey', height=H // k, width=W // k)
can1.pack()

x1, y1 = z[0] // k, z[1] // k
terre = can1.create_oval(x1,y1 , x1 + 20, y1 + 20, width=2, fill='blue')
R = 6963400000
R=R*5
x1, y1=(a - R) // k, (b - R) // k
x2, y2=(a + R) // k, (b + R) // k
soleil = can1.create_oval(x1, y1, x2, y2, width=2, fill='yellow')

# Lancement de la fonction Euler
Euler()
# Boucle principale:
fen1.mainloop()