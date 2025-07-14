Welcome to my final project for the Advanced Programming course I am taking in Bar-Ilan University.
about the project:
Over the course of 6 assignments we planned a web application which realizes a computational grpah from a customizable static configuration file.
In this project I learned much about SOLID design principles, which is an excellent way to plan a large project:
-Single responsibility: In each assignment we built towards a subgoal by dividing the problem into small classes, each handling a small aspect of the goal.
-Interface segregation: by building this project from small subgoals, we were able to construct many building blocks which we were able to use repeatedly in many ways with custom extensions to suit our needs.
For example, by defining the interface 'Agent' we were able to plan a graph for many types of different operator modules which implement agent (such as PlusAgent and Inc agent).
Dependency inversion: In this project we realized the Observer pattern using the interface of agent (observer), which has a callback and a publish function, which are customizeable based in the agent implementation.
The Topic class (Observable) is a simple medium: some Agent activates its publish for some Message object. The function, in turn, activates all subscribed agents' callback functions with the recieved message, and they do with it as they please.
This allows full flexibility for observer design pattern implementations.

In this project we also touched on server design: we designed a toy HTTP server, which recieves real HTTP requests from the browser.
The server designates appropriate servlets to handle different HTTP requests efficiently (keeping in mind single-responsibilty, interface segregation and dependency inversion).
Each request is processed in a threadpool, which allows efficient resource management for the server.

Another imortant principle in the server design was the use of MVVM architecture:
Our code which realizes agents, topics, graphs, etc is the core of the model.
The server serves as the ViewModel, by mapping different servers to special servlets which are designated to handle incoming HTTP requests and output an HTTP response to the client by calling relevant model functions.
The view was also a very educational aspect of the project: by designing the view in static HTML resource files, which are utilized by the servlets, we allow dynamic view developmnet without the need to recompile the program everytime. This was another very interesting aspect of the single responsibility principle. 

Usage instructions:
The program runs on the localhost (8080) port.
Complie the program and control via a web browser at the address http://localhost:8080/app/index.html
Select and deploy your custom config, and control the inputs (yellow boxes) using the right form.
A config is a text file with a ".conf" suffix.
The file consists of line triplets: the first line will be the operator class (with path and package references), next line will be input nodes, the line after that output nodes.
Note that input/output nodes are uniquely identified by name, so if one operator module outputs to 'x', then we can wire the output to another module using 'x'.
The project currently supports an increment module (configs.IncAgent), and addition agents (PlusAgent), but more may be added in the future.
