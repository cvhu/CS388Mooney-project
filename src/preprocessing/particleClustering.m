function particleClustering()
all = dlmread('rr10.mat');
run(all, 10, 1000);

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

% ds = zeros(1,yn);
% ds(1) = dist(mean(X), topic);

for i = 1:(yn-1)
    topics = yearTopics((i*K+1):((i+1)*K),:);
    Z = linkage(X);    
    dendrogram(Z);
    C = cluster(Z, 'maxclust', 10);
    C = classifyTopic(X, C, topics, K);
    err = 0.0;
    for j = 1:N
        yi = i*K + C(j);
        topic = yearTopics(yi,:);        
        err = err + dist(X(j,:), topic);
        X(j,:) = shift2topic(X(j,:), topic);        
    end
    display(err/N);
end

function Cout = classifyTopic(X,Cin,topics, K)
Cout = Cin;
for k = 1:K
    ind = (Cin==k);
    Xk = X(ind,:);
    m = dist(mean(Xk),topics(1,:));
    mk = 1;
    for j = 2:K
        c = dist(mean(Xk),topics(1,:));
        if(c < m)
            m = c;
            mk = j;
        end
    end
    Cout(ind) = mk;
end

function d = dist(x, y)
d = 1 - (x*y')/(norm(x,2)*norm(y,2));

function X = sampleTopic(topic)
sigma = std(topic);
X = topic + sigma*randn(size(topic));

function Xout = shift2topic(Xin, topic)
Xout = 0.5*Xin + topic;