function particleClustering()
close all
clear all
all = dlmread('rr10.mat');
run(all, 10, 500);

function run(yearTopics, K, N)

[h, w] = size(yearTopics);
yn = h/K;  % number of years

% Initialize X
% each row represents a particle
% each particle is a vector of size w
% w is the vocabulary size
X = zeros(N,w);
err = 0.0;
for i = 1:N   
   yi = mod(i,K)+1;
   X(i,:) = sampleTopic(yearTopics(yi,:)); 
   err = err + dist(X(i,:), yearTopics(yi,:));
end
ds = zeros(1,yn);
ds(1) = err/N;


for i = 1:(yn-1)
    topics = yearTopics((i*K+1):((i+1)*K),:);
    
    Z = linkage(X, 'complete', 'cosine');    
    dendrogram(Z);
    C = cluster(Z, 'maxclust', 10);
    Xc = X;% centroids(X, C, K);
    
%     [C, Xc] = kmeans(X, K);

    C = classifyTopic(Xc, C, topics, K);
    err = 0.0;
    for j = 1:N
        yi = i*K + C(j);
        topic = yearTopics(yi,:);        
        err = err + dist(X(j,:), topic);
        X(j,:) = shift2topic(X(j,:), topic);        
    end
    ds(i+1) = err/N;
end

figure
plot(ds);

function Cout = classifyTopic(Xc,Cin,topics, K)
Cout = Cin;
for k = 1:K
    ind = (Cin==k);
    Xk = Xc(k,:);
    m = dist(Xk,topics(1,:));
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

% function Xout = centroids(Xin, C, K)


function d = dist(x, y)
d = 1 - (x*y')/(norm(x,2)*norm(y,2));

function X = sampleTopic(topic)
sigma = 0.5*std(topic);
X = topic + sigma*randn(size(topic));

function Xout = shift2topic(Xin, topic)
sigma = 0.5*std(Xin);
Xout = 0.1*Xin + topic + sigma*randn(size(Xin));