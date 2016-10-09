import scipy as sp
import matplotlib as mpl
import matplotlib.pyplot as plt
import pdb

data = sp.genfromtxt("data/timingData.csv", delimiter=",", skip_header = 1)
print(data.shape)
print(data[:,:10])
factor = data.shape[0]/100
avg_data = sp.zeros((100, data.shape[1]))
# pdb.set_trace()
for i in sp.arange(0,100):
    avg_data[i,:] = sp.mean(data[i*factor: (i+1) * factor, :], axis = 0)
print(avg_data)
particleNumbers = data[:,0]
diffuseTimes = data[:,1]
jumpCalls = data[:,2]
attachTimes = data[:,3]

avg_diffTimes = avg_data[:,1]
plt.figure(0)
plt.scatter(sp.arange(0,100),avg_diffTimes)
plt.legend(["avg_diffTimes"])
plt.show();



# plt.figure(0)
# plt.scatter(particleNumbers, diffuseTimes)
# plt.legend(["diffuse times"])
# plt.figure(1)
# plt.scatter(particleNumbers, jumpCalls)
# plt.legend(["jump calls"])
# plt.figure(2)
# plt.scatter(particleNumbers, attachTimes)
# plt.legend(["attach times"])
# plt.show();
# 
# 
# 
