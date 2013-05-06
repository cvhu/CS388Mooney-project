function particleClustering()
close all
clear all
all = dlmread('rr10.mat');
K = 10;
N = 100;
Y = size(all,1)/K;
gamma(K,N,Y,all,0.5);
alpha(K,N,Y,all,0.5);

% dists = {'cosine', 'sqEuclidean', 'cityblock', 'correlation'};
% D = length(dists);
% dss = zeros(D,Y);
% for d = 1:D
%     dss(d,:) = run(all, K, N, dists{1,d}, 'kmeans');
% end
% plot(dss')


% dss = zeros(L,Y);
% for l = 1:L
%     dss(l,:) = run(all, K, N, dists{1,1}, links{1,l}, alpha, gamma);
% end
% semilogy(dss')

function alpha(K,N,Y,all,gamma)
ps = 0:0.25:1;
L = length(ps);
dss = zeros(L+1,Y);
dss(1,:) = baseline(all, K);
for l = 1:L
    dss(l+1,:) = run(all, K, N, 'cosine', 'single', ps(l), gamma);
end
figure
plot(dss(:,2:Y)');
ylabel('Avg Error');
xlabel('Year');
title('Learning Curve of various \alpha');
legend('baseline', '0.0', '0.25', '0.5', '0.75', '1.0');


function gamma(K,N,Y,all,alpha)
ps = 0:0.25:1;
L = length(ps);
dss = zeros(L+1,Y);
dss(1,:) = baseline(all, K);
for l = 1:L
    dss(l+1,:) = run(all, K, N, 'cosine', 'single', alpha, ps(l));
end
figure
plot(dss(:,2:Y)');
ylabel('Avg Error');
xlabel('Year');
title('Learning Curve of various \gamma');
legend('baseline', '0.0', '0.25', '0.5', '0.75', '1.0');



function ds = baseline(yearTopics, K)
Y = size(yearTopics)/K;
ds = zeros(1,Y);
for y = 2:Y
    prev = yearTopics(((y-2)*K+1):(y-1)*K, :);
    curr = yearTopics(((y-1)*K+1):y*K, :);
    err = 0.0;
    for k = 1:K
        pi = prev(k,:);
        mk = bestTopic(pi, curr);
        err = err + distance(pi, curr(mk,:));
    end
    ds(y) = err/K;
end


% kmeans:
% dist = cosine, sqEuclidean, cityblock, correlation
% link = kmeans
% 
% hac:
% dist = euclidean, cityblock, chebychev, cosine, correlation, jaccard
% link = single, complete, average, weighted

function [ds, Z] = run(yearTopics, K, N, dist, link, alpha, gamma)

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
   X(i,:) = sampleTopic(yearTopics(yi,:), alpha); 
   err = err + distance(X(i,:), yearTopics(yi,:));
end
ds = zeros(1,yn);
ds(1) = err/N;


for i = 1:(yn-1)
    topics = yearTopics((i*K+1):((i+1)*K),:);    
    if(strcmp(link, 'kmeans'))
        [C, Xc] = kmeans(X, K, 'distance', dist);
        Z = C;
    else
        Z = linkage(X, link, dist);
%         dendrogram(Z);
        C = cluster(Z, 'maxclust', 10);
        Xc = centroids(X, C, K);
    end
    C = classifyTopic(Xc, C, topics, K);
    err = 0.0;
    for j = 1:N
        yi = i*K + C(j);
        topic = yearTopics(yi,:);        
        err = err + distance(X(j,:), topic);
        X(j,:) = shift2topic(X(j,:), topic, alpha, gamma);        
    end
    ds(i+1) = err/N;
end

% figure
% plot(ds);

function Cout = classifyTopic(Xc,Cin,topics, K)
Cout = Cin;
for k = 1:K
    ind = (Cin==k);
    Xk = Xc(k,:);    
    Cout(ind) = bestTopic(Xk, topics);
end

function mk = bestTopic(x, topics)
m = distance(x,topics(1,:));
mk = 1;
for j = 2:size(topics,1)
    c = distance(x,topics(j,:));
    if(c < m)
        m = c;
        mk = j;
    end
end

function Xout = centroids(Xin, C, K)
V = size(Xin,2);
Xout = zeros(K,V);
for k = 1:K
    Xout(k,:) = mean(Xin(C==k,:));
end


function d = distance(x, y)
d = 1 - (x*y')/(norm(x,2)*norm(y,2));

function X = sampleTopic(topic, alpha)
sigma = alpha*std(topic);
X = topic + sigma*randn(size(topic));

function Xout = shift2topic(Xin, topic, alpha, gamma)
sigma = alpha*std(topic);
Xout = gamma*Xin + topic + sigma*randn(size(Xin));