# DLA

Diffusion Limited Aggregation (DLA): A seed particle is placed at the origin. One by one particles diffuse in a Brownian motion (or if you're looking at discrete DLA, a symmetric random walk on a lattice) until they enounter the existing fixed structure (the aggregate). When they hit this structure, they attach to it and a new particle is released from very far away. 

I did a project on the fractal properties of DLA at university, but wrote it in MATLAB which was quite slow. This is my first Java project, so there's a lot of room for improvement. I am experimenting with quadtrees and kd tree to improve collision detection speed, in order to make bigger aggregates.
