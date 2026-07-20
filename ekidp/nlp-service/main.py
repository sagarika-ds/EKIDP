from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional
import uvicorn
from nlp_engine import (
    extract_entities,
    extract_topics,
    extract_keywords,
    calculate_risk_score,
    extract_summary,
    analyze_sentiment
)

app = FastAPI(
    title="EKIDP NLP Service",
    description="NLP Engine for Entity Extraction and Text Analysis",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"]
)

class TextRequest(BaseModel):
    text: str
    document_id: Optional[int] = None
    document_type: Optional[str] = "general"

class NLPResponse(BaseModel):
    document_id: Optional[int]
    entities: dict
    topics: list
    keywords: list
    summary: str
    sentiment: str
    risk_score: float
    word_count: int
    sentence_count: int

@app.get("/health")
def health():
    return {"status": "UP", "service": "EKIDP NLP Engine"}

@app.post("/analyze", response_model=NLPResponse)
def analyze_text(request: TextRequest):
    if not request.text or len(request.text.strip()) == 0:
        raise HTTPException(status_code=400, detail="Text cannot be empty")

    try:
        entities = extract_entities(request.text)
        topics = extract_topics(request.text)
        keywords = extract_keywords(request.text)
        summary = extract_summary(request.text)
        sentiment = analyze_sentiment(request.text)
        risk_score = calculate_risk_score(request.text)
        word_count = len(request.text.split())
        sentence_count = len([s for s in request.text.split('.')
                               if s.strip()])

        return NLPResponse(
            document_id=request.document_id,
            entities=entities,
            topics=topics,
            keywords=keywords,
            summary=summary,
            sentiment=sentiment,
            risk_score=risk_score,
            word_count=word_count,
            sentence_count=sentence_count
        )
    except Exception as e:
        raise HTTPException(status_code=500,
                            detail=f"Analysis failed: {str(e)}")

@app.post("/entities")
def get_entities(request: TextRequest):
    if not request.text:
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    return {"entities": extract_entities(request.text)}

@app.post("/keywords")
def get_keywords(request: TextRequest):
    if not request.text:
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    return {"keywords": extract_keywords(request.text)}

@app.post("/risk-score")
def get_risk_score(request: TextRequest):
    if not request.text:
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    score = calculate_risk_score(request.text)
    level = "HIGH" if score > 60 else "MEDIUM" if score > 30 else "LOW"
    return {
        "risk_score": score,
        "risk_level": level,
        "text_length": len(request.text)
    }

@app.post("/summary")
def get_summary(request: TextRequest):
    if not request.text:
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    return {
        "summary": extract_summary(request.text),
        "sentiment": analyze_sentiment(request.text),
        "word_count": len(request.text.split())
    }

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True)

from ml_models import (
    predict_project_success,
    predict_employee_attrition,
    predict_cost_overrun,
    predict_delivery_delay
)

class ProjectSuccessRequest(BaseModel):
    team_size: float
    doc_coverage: float
    risk_score: float
    budget_health: float
    experience_years: float

class AttritionRequest(BaseModel):
    tenure_years: float
    project_count: float
    risk_score: float
    satisfaction: float
    workload: float

class CostOverrunRequest(BaseModel):
    project_complexity: float
    team_experience: float
    scope_changes: float
    timeline_pressure: float
    vendor_dependency: float

class DelayRequest(BaseModel):
    team_size: float
    doc_coverage: float
    risk_score: float
    scope_changes: float
    timeline_pressure: float

@app.post("/predict/project-success")
def predict_success_endpoint(request: ProjectSuccessRequest):
    return predict_project_success(
        request.team_size, request.doc_coverage,
        request.risk_score, request.budget_health,
        request.experience_years
    )

@app.post("/predict/attrition")
def predict_attrition_endpoint(request: AttritionRequest):
    return predict_employee_attrition(
        request.tenure_years, request.project_count,
        request.risk_score, request.satisfaction,
        request.workload
    )

@app.post("/predict/cost-overrun")
def predict_cost_endpoint(request: CostOverrunRequest):
    return predict_cost_overrun(
        request.project_complexity, request.team_experience,
        request.scope_changes, request.timeline_pressure,
        request.vendor_dependency
    )

@app.post("/predict/delivery-delay")
def predict_delay_endpoint(request: DelayRequest):
    return predict_delivery_delay(
        request.team_size, request.doc_coverage,
        request.risk_score, request.scope_changes,
        request.timeline_pressure
    )
