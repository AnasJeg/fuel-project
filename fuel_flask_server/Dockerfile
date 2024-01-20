FROM python:3.9-slim-buster
WORKDIR /src
COPY src/requirements.txt ./
RUN pip install -r requirements.txt
COPY . .
EXPOSE 8070
CMD ["python", "-u", "src/app.py"]