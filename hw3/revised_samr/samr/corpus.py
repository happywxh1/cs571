import os
import csv
import random

from samr.data import Datapoint
from samr.settings import DATA_PATH

def _iter_data_file(filename):
    path = os.path.join(DATA_PATH, filename)
    it = csv.reader(open(path, "r"), delimiter="\t")
    row = next(it)  # Drop column names
    if " ".join(row[:3]) != "PhraseId SentenceId Phrase":
        raise ValueError("Input file has wrong column names: {}".format(path))

    for row in it:
        if len(row) == 3:
            row += (None,)
        yield Datapoint(*row)


def _iter_data_file2(filename):
    path = os.path.join(DATA_PATH, filename)
    it = csv.reader(open(path, "r"), delimiter="\t")
    row = next(it)  # Drop column names
    if " ".join(row[:3]) != "PhraseId SentenceId Phrase":
        raise ValueError("Input file has wrong column names: {}".format(path))

    ll1=[]
    ll2=[]
    for row in it:
        if len(row) == 3:
            row += (None,)
        yield Datapoint(*row)
        if len(row) >3:
            ll1.append(row[2])
            ll2.append(row[3])
        
    fdict = open(os.path.join(DATA_PATH, "train-dict.txt"))
    dict=[]
    l=[]
    i=0
    
    for lines in fdict:
        a=lines.strip().split(" ")
        if a[1]==i:
            l.append(a[0])
        else:
            dict.append(l)
            i=i+1
            l=[]

    i=0
    for dicl in dict:
        flag=0
        for item in dicl:
            if item in ll1:
                flag=1
                label=ll2[ll1.index(item)]
                break
        if flag==1:
            nr=[]
            for item in dicl:
                nr=[156060+i, 8545+i, item, label]
                i+=1
                yield Datapoint(*nr)


def iter_corpus(__cached=[]):
    """
    Returns an iterable of `Datapoint`s with the contents of train.tsv.
    """
    if not __cached:
        __cached.extend(_iter_data_file2("train.tsv"))
    return __cached


def iter_test_corpus():
    """
    Returns an iterable of `Datapoint`s with the contents of test.tsv.
    """
    return list(_iter_data_file("test.tsv"))


def make_train_test_split(seed, proportion=0.9):
    """
    Makes a randomized train/test split of the train.tsv corpus with
    `proportion` fraction of the elements going to train and the rest to test.
    The `seed` argument controls a shuffling of the corpus prior to splitting.
    The same seed should always return the same train/test split and different
    seeds should always provide different train/test splits.

    Return value is a (train, test) tuple where train and test are lists of
    `Datapoint` instances.
    """
    data = list(iter_corpus())
    ids = list(sorted(set(x.sentenceid for x in data)))
    if len(ids) < 2:
        raise ValueError("Corpus too small to split")
    N = int(len(ids) * proportion)
    if N == 0:
        N += 1
    rng = random.Random(seed)
    rng.shuffle(ids)
    test_ids = set(ids[N:])
    train = []
    test = []
    for x in data:
        if x.sentenceid in test_ids:
            test.append(x)
        else:
            train.append(x)
    return train, test
