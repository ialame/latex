from tkinter import *
import numpy as np


def f(z):
    rts = np.sqrt((z[0] - a) ** 2 + (z[1] - b) ** 2)
    Ats = G * Ms / rts ** 2
    Atsx, Atsy = -Ats * (z[0] - a) / rts, -Ats * (z[1] - b) / rts
    rlt = np.sqrt((z[4] - z[0]) ** 2 + (z[5] - z[1]) ** 2)
    Alt = G * Mt / rlt ** 2
    Altx, Alty = -Alt * (z[4] - z[0]) / rlt, -Alt * (z[5] - z[1]) / rlt
    Atl = G * Ml / rlt ** 2
    Atlx, Atly = -Atl * (z[0] - z[4]) / rlt, -Atl * (z[1] - z[5]) / rlt
    rls = np.sqrt((z[4] - a) ** 2 + (z[5] - a) ** 2)
    Als = G * Ms / rls ** 2
    Alsx, Alsy = -Als * (z[4] - a) / rls, -Als * (z[5] - b) / rls
    return np.array([z[2], z[3], Atsx + Atlx, Atsy + Atly, z[6], z[7], Altx + Alsx, Alty + Alsy])


def Euler():
    global z, h
    # z=z+h*f(z)
    # z = z + h / 2 * (f(z) + f(z + h * f(z)))
    k1 = f(z)
    k2 = f(z + h / 2 * k1)
    k3 = f(z + h / 2 * k2)
    k4 = f(z + h * k3)
    z = z + h * (k1 + 2 * k2 + 2 * k3 + k4) / 6
    # déplacement de la balle
    can1.coords(oval1, z[0] // k, z[1] // k, z[0] // k + 20, z[1] // k + 20)
    can1.coords(oval2, (z[4] + 30 * (z[4] - z[0])) // k, (z[5] + 30 * (z[5] - z[1])) // k,
                (z[4] + 30 * (z[4] - z[0])) // k + 10, (z[5] + 30 * (z[5] - z[1])) // k + 10)
    # boucler, après 50 millisecondes
    fen1.after(1, Euler)


# ========== Programme principal =============

H, W = 4.0E11, 4.0E11
a, b = W / 2, H / 2  # centre de la terre
k = 4.0E8  # 1 px = 50 km
# coordonnées initiales
x0, y0 = a + 1.47E11, H / 2
# vitesse initiale
Ms = 1.989E30
Mt = 5.972E24
Ml = 7.36E22
G = 6.67E-11
V0 = 30200  # 28000#29790
alpha = np.pi / 2
R = 6963400000 * 5
V0L = V0 + 3680 * 1000 / 3600
Dtl = 400000000
z = np.array([x0, y0, V0 * np.cos(alpha), -V0 * np.sin(alpha), x0 + Dtl, y0, V0L * np.cos(alpha), -V0L * np.sin(alpha)])
# 'pas' du temps
h = 24 * 3600 / 24
# Création de la fenêtre principale :
fen1 = Tk()
fen1.title("Soleil-Terre-Lune")
# création du canvas :
can1 = Canvas(fen1, bg='dark grey', height=H // k, width=W // k)
can1.pack()
# création de la balle
oval1 = can1.create_oval(z[0] // k, z[1] // k, z[0] // k + 20, z[1] // k + 20, width=2, fill='blue')
oval0 = can1.create_oval((a - R) // k, (b - R) // k, (a + R) // k, (b + R) // k, width=2, fill='yellow')
oval2 = can1.create_oval(z[4] // k, z[5] // k, z[4] // k + 10, z[5] // k + 10, width=2, fill='white')

# Lancement de la fonction Euler
Euler()
# démarrage de la boucle principale:
fen1.mainloop()