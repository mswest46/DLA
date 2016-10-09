# DLA

Diffusion Limited Aggregation (DLA): A seed particle is placed at the origin. One by one particles diffuse in a Brownian motion until they enounter the existing fixed structure. When they hit this structure, they attach to it and a new particle is released from very far away. 

I am experimenting with quadtrees and kd tree to improve collision detection speed, in order to make bigger aggregates. This is my first Java project, so it's just for playing around. To make it work, 1. run make from root directory, 2. java -cp ./bin exampleDLA. That should do it (hopefully).
