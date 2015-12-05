Sentiment Analysis on Movie Reviews
===================================

The implementation is based on the [samr](https://github.com/rafacarrascosa/samr), which gives quite high rank in the kaggle competition. The installation of the package can be found at the original website.

Based on the original implementation, we experimented with adding the TF-IDF features, add the logistic regression classifiers. The main improvement is to add K-means word clustering from the word2vec embedding. The word classes are used in both training and prediction step, which could obviouly improve the accuracy.
