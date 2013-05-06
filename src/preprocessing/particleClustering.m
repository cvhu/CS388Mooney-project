function particleClustering()
all = dlmread('rr10.mat');
run(all, 10, 100);

function run(yearTopics, K, N)

[h, w] = size(yearTopics);
yn = h/K;  % number of years

% Initialize X
% each row represents a particle
% each particle is a vector of size w
% w is the vocabulary size
X = zeros(N,w);
for i = 1:N
   yi = mod(i,K)+1;
   X(i,:) = sampleTopic(yearTopics(yi,:)); 
end
display(yn);
% display(X);


function x = sampleTopic(topic)
sigma = std(topic);
x = topic + sigma*randn(size(topic));