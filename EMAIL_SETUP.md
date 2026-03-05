# Configuration Email pour les Donations - Minolingo

## 📧 Configuration actuelle (minolingo.online)

Le système est déjà configuré pour utiliser votre propre serveur email :

```properties
# Configuration SMTP Minolingo (déjà configurée)
spring.mail.host=minolingo.online
spring.mail.port=587
spring.mail.username=noreply@minolingo.online
spring.mail.password=votre-mot-de-passe-email
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.trust=*
spring.mail.from=noreply@minolingo.online
```

## 🔧 Actions requises

### 1. Mettre à jour le mot de passe

Remplacez `votre-mot-de-passe-email` par le vrai mot de passe du compte `noreply@minolingo.online` :

```properties
spring.mail.password=LE_VRAI_MOT_DE_PASSE
```

### 2. Vérifier la configuration SMTP

Assurez-vous que le serveur SMTP sur `minolingo.online:587` est :
- ✅ Actif et accessible
- ✅ Configuré pour accepter les connexions authentifiées
- ✅ Compatible avec STARTTLS

## 🎯 Fonctionnalité implémentée

### Email envoyé automatiquement lors de la création d'un don

Quand un utilisateur crée un don :
- ✅ Le don est sauvegardé en base de données
- ✅ Un email de remerciement est envoyé automatiquement depuis `noreply@minolingo.online`
- ✅ L'email contient les détails du don (article, quantité, date)

### Contenu de l'email

- **Design moderne et responsive**
- **Personnalisé avec les détails du don**
- **Message de remerciement chaleureux**
- **Informations sur l'impact du don**
- **Branding Minolingo**

## 📝 Exemple d'email envoyé

**De :** noreply@minolingo.online  
**À :** email-de-lutilisateur@example.com  
**Sujet :** Merci pour votre généreux don !

Contenu HTML personnalisé avec :
- Détails du don (nom, quantité, date)
- Message de remerciement
- Impact social du don
- Footer Minolingo

## 🚀 Pour déployer en production

1. **Mettez à jour le mot de passe** dans `application.properties`
2. **Redémarrez le serveur** pour appliquer les changements
3. **Testez** en créant un nouveau don

Les emails seront envoyés automatiquement depuis votre propre domaine `minolingo.online` !

## � Test de configuration

Pour vérifier que tout fonctionne :

```bash
# Test de connexion SMTP
telnet minolingo.online 587
```

Ou regardez les logs du serveur après avoir créé un don :

```
Thank you email sent to: email-de-lutilisateur@example.com
```
