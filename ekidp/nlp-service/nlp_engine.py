import spacy
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize, sent_tokenize
from nltk.probability import FreqDist
from collections import Counter
import re

nltk.download('punkt', quiet=True)
nltk.download('stopwords', quiet=True)
nltk.download('averaged_perceptron_tagger', quiet=True)
nltk.download('punkt_tab', quiet=True)

try:
    nlp = spacy.load("en_core_web_sm")
except Exception:
    import subprocess
    subprocess.run(["python", "-m", "spacy", "download", "en_core_web_sm"])
    nlp = spacy.load("en_core_web_sm")

TECH_KEYWORDS = [
    "python", "java", "javascript", "typescript", "react", "angular", "vue",
    "spring", "django", "fastapi", "nodejs", "docker", "kubernetes", "aws",
    "azure", "gcp", "postgresql", "mysql", "mongodb", "redis", "kafka",
    "microservices", "rest", "graphql", "machine learning", "deep learning",
    "artificial intelligence", "nlp", "data science", "cloud", "devops",
    "ci/cd", "git", "linux", "terraform", "ansible", "jenkins", "neo4j",
    "elasticsearch", "spark", "hadoop", "tableau", "power bi"
]

RISK_KEYWORDS = [
    "risk", "critical", "urgent", "issue", "problem", "concern", "threat",
    "vulnerability", "failure", "error", "bug", "incident", "outage",
    "breach", "delay", "overrun", "exceeded", "missed", "blocked",
    "dependency", "bottleneck", "escalation", "compliance", "audit"
]

SKILL_KEYWORDS = [
    "experience", "expertise", "proficient", "skilled", "knowledge",
    "certified", "trained", "ability", "competency", "qualification",
    "years of experience", "background in", "specialization"
]

def extract_entities(text):
    doc = nlp(text)
    entities = {
        "people": [],
        "organizations": [],
        "locations": [],
        "dates": [],
        "technologies": [],
        "risks": [],
        "skills": [],
        "topics": [],
        "keywords": []
    }

    for ent in doc.ents:
        if ent.label_ == "PERSON" and ent.text not in entities["people"]:
            entities["people"].append(ent.text)
        elif ent.label_ == "ORG" and ent.text not in entities["organizations"]:
            entities["organizations"].append(ent.text)
        elif ent.label_ in ["GPE", "LOC"] and ent.text not in entities["locations"]:
            entities["locations"].append(ent.text)
        elif ent.label_ == "DATE" and ent.text not in entities["dates"]:
            entities["dates"].append(ent.text)

    text_lower = text.lower()
    for tech in TECH_KEYWORDS:
        if tech in text_lower and tech not in entities["technologies"]:
            entities["technologies"].append(tech)

    for risk in RISK_KEYWORDS:
        if risk in text_lower and risk not in entities["risks"]:
            entities["risks"].append(risk)

    for skill in SKILL_KEYWORDS:
        if skill in text_lower and skill not in entities["skills"]:
            entities["skills"].append(skill)

    entities["topics"] = extract_topics(text)
    entities["keywords"] = extract_keywords(text)

    return entities

def extract_topics(text):
    doc = nlp(text)
    noun_phrases = [chunk.text.lower() for chunk in doc.noun_chunks
                    if len(chunk.text) > 3]
    topic_freq = Counter(noun_phrases)
    return [topic for topic, count in topic_freq.most_common(10)]

def extract_keywords(text):
    try:
        stop_words = set(stopwords.words('english'))
        tokens = word_tokenize(text.lower())
        filtered = [w for w in tokens
                    if w.isalpha() and w not in stop_words and len(w) > 3]
        freq = FreqDist(filtered)
        return [word for word, count in freq.most_common(15)]
    except Exception:
        return []

def calculate_risk_score(text):
    text_lower = text.lower()
    risk_count = sum(1 for risk in RISK_KEYWORDS if risk in text_lower)
    total_words = len(text.split())
    if total_words == 0:
        return 0
    score = min(100, (risk_count / max(total_words / 100, 1)) * 50)
    return round(score, 2)

def extract_summary(text, num_sentences=3):
    try:
        sentences = sent_tokenize(text)
        if len(sentences) <= num_sentences:
            return text
        stop_words = set(stopwords.words('english'))
        words = word_tokenize(text.lower())
        filtered = [w for w in words if w.isalpha() and w not in stop_words]
        freq = FreqDist(filtered)
        sentence_scores = {}
        for sentence in sentences:
            for word in word_tokenize(sentence.lower()):
                if word in freq:
                    if sentence not in sentence_scores:
                        sentence_scores[sentence] = 0
                    sentence_scores[sentence] += freq[word]
        summary_sentences = sorted(sentence_scores,
                                   key=sentence_scores.get,
                                   reverse=True)[:num_sentences]
        return ' '.join(summary_sentences)
    except Exception:
        return text[:500]

def analyze_sentiment(text):
    positive_words = ["good", "great", "excellent", "success", "achieved",
                      "completed", "improved", "positive", "benefit", "efficient"]
    negative_words = ["bad", "poor", "failed", "issue", "problem", "risk",
                      "concern", "delayed", "overrun", "critical", "urgent"]
    text_lower = text.lower()
    pos_count = sum(1 for w in positive_words if w in text_lower)
    neg_count = sum(1 for w in negative_words if w in text_lower)
    if pos_count > neg_count:
        return "POSITIVE"
    elif neg_count > pos_count:
        return "NEGATIVE"
    return "NEUTRAL"
