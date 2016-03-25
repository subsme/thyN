package com.thyn.backend.utilities;

import com.thyn.backend.entities.users.User;

public class EmailManager {

    private static GAEEmailIntegration mailProvider = new GAEEmailIntegration("F2RsCJMbBa7HPaMAPQhV", "NaXkn6uZGn9AakNaUGxg");
    private static String PreHtmlEmailContent = "<table bgcolor='#ff5f25' border='0' cellpadding='0' cellspacing='0' width='100%'><tbody><tr><td><table align='center' cellpadding='0' cellspacing='0' width='600'><tbody><tr><td height='30' valign='top'></td></tr><tr><td bgcolor='#ffffff' height='172' valign='top'><table align='center' border='0' cellpadding='0' cellspacing='0' width='550'><tbody><tr><td height='160'><a href='http://mmarazzu.yanchware.com' target='_blank'><img alt='Mmarazzu logo' border='0' src='http://mmarazzu.yanchware.com/img/logoEmail.png' height='54'></a></td></tr></tbody></table></td></tr><tr><td bgcolor='#ffffff' valign='top'>";
    private static String PostHtmlEmailContent = "</td></tr><tr><td bgcolor='#fff' height='32' valign='top'></td></tr><tr><td height='32' style='font-size:9px;line-height:2;color:#262626;font-family:Arial,Arial,Helvetica,sans-serif;line-height:2;text-align:right;padding-right:10px' valign='top'></td></tr></tbody></table></td></tr></tbody></table>";

    public static boolean inviteNewUser(String email, String workspaceRoleTitle, User invitingUser, String validationKey)
    {
        try {
            String invitingUserName = invitingUser.getName();
            String invitingUserProfileUrl = "https://mmarazzu.yanchware.com/viewprofile?userKey="+invitingUser.getId();

            String title = " invited you to join Mmarazzu!";
            String contentPreActionLink[] = {invitingUserName+" is offering you the position of "+workspaceRoleTitle+" in the Workspace called "+
                    "If you are interested, accept this offer ", " and start to create!\n"};
            String urlActionLink = "http://mmarazzu.yanchware.com/completeregistration?validationKey="+validationKey+"&email="+email;
            String titleActionLink = "Accept Invitation";
            String contentPostActionLinkString = "We're looking forward to welcoming you to our community and help you developing your ideas!";
            String footers[] = {"If you want to see who "+invitingUserName+" is, you can visit this page: "+invitingUserProfileUrl+" . ",""};

            mailProvider.setTo(email)
                    .setFrom("contacts@yanchware.com")
                    .setSubject("[Mmarazzu] Invitation on Mmarazzu")
                    .setText(invitingUserName + title+"\n"+
                            contentPreActionLink[0]+".\n" +
                            contentPreActionLink[1]+"visiting the following page: "+urlActionLink + contentPreActionLink[2]+
                            contentPostActionLinkString+"\n\nMmarazzu Team @ YanchWare\n\n"+ footers[0] + "\n" + footers[1])
                    .setHtml(createBodyHtmlEmail(title, contentPreActionLink[0] +"<br/>"+contentPreActionLink[1]+ " clicking on the following button "+contentPreActionLink[2], urlActionLink, titleActionLink, contentPostActionLinkString, invitingUserProfileUrl, invitingUserName))
                    .send();

            return mailProvider.message == "success";
        } catch (Exception e) {
            Logger.logError("Exception while sending invitation email to " + email + ".", e);
        }
        return false;
    }

    public static boolean sendVerificationEmail(String email, String validationKey)
    {
        try {
            String title = "Congratulations! Your account on thyN has been created.";
            String contentPreActionLink[] = {"Before being able to use it, you need to confirm your subscription", "Complete your account setup and start to create!"};
            String urlActionLink = "http://mmarazzu.yanchware.com/completeregistration?validationKey="+validationKey+"&email="+email;
            String titleActionLink = "Complete Account Setup";
            String contentPostActionLinkString = "We're looking forward to welcoming you to our community and help you developing your ideas!";

            mailProvider.setTo(email)
                    .setFrom("contacts@yanchware.com")
                    .setSubject("[Mmarazzu] Welcome!")
                    .setText(title+"\n"+
                            contentPreActionLink[0]+" visiting the page at "+urlActionLink+" . "+contentPreActionLink[1]+"\n"+
                            contentPostActionLinkString+"\n\nMmarazzu Team @ YanchWare\n\n")
                    .setHtml(createBodyHtmlEmail(title, contentPreActionLink[0] + " clicking on the following button. "+contentPreActionLink[1], urlActionLink, titleActionLink, contentPostActionLinkString))
                    .send();

            return mailProvider.message == "success";
        } catch (Exception e) {
            Logger.logError("Exception while sending verification email to " + email + ".", e);
        }
        return false;
    }

    private static String createBodyHtmlEmail(String title, String contentPreActionLink, String urlActionLink, String titleActionLink, String contentPostActionLinkString)
    {
        return PreHtmlEmailContent + "<table align='center' border='0' cellpadding='0' cellspacing='0' width='550'><tbody><tr><td style='font-size:12px;line-height:2;color:#262626;font-family:Arial,Arial,Helvetica,sans-serif'><p style='color:#54a43c;font-size:22px;line-height:1.5'>"+
                "<strong>"+title+"</strong></p><p>"+contentPreActionLink+"</p><table align='center' bgcolor='#ff5f25' border='0' cellpadding='0' cellspacing='2' width='280'><tbody><tr><td align='center' bgcolor='orange'>"+
                "<a href='"+urlActionLink+"' style='color:#ffffff;font-family:Arial,Arial,Helvetica,sans-serif;font-size:20px;text-decoration:none;display:block;padding-top:12px;padding-bottom:12px' target='_blank'><strong>"+
                titleActionLink+"</strong></a></td></tr></tbody></table><p>"+contentPostActionLinkString+"</p><br><strong>Mmarazzu Team @ YanchWare</strong><br></td></tr></tbody></table>" + PostHtmlEmailContent;
    }

    private static String createBodyHtmlEmail(String title, String contentPreActionLink, String urlActionLink, String titleActionLink, String contentPostActionLinkString, String invitingUserProfileUrl, String invitingUserName)
    {
        return PreHtmlEmailContent + "<table align='center' border='0' cellpadding='0' cellspacing='0' width='550'><tbody><tr><td style='font-size:12px;line-height:2;color:#262626;font-family:Arial,Arial,Helvetica,sans-serif'><p style='color:#54a43c;font-size:22px;line-height:1.5'>"+
                "<a href=\""+invitingUserProfileUrl+"\"><img height=\"50px\"src=\""+"\"/> "+ invitingUserName +"</a><strong>"+title+"</strong></p><p>"+contentPreActionLink+"</p><table align='center' bgcolor='#ff5f25' border='0' cellpadding='0' cellspacing='2' width='280'><tbody><tr><td align='center' bgcolor='orange'>"+
                "<a href='"+urlActionLink+"' style='color:#ffffff;font-family:Arial,Arial,Helvetica,sans-serif;font-size:20px;text-decoration:none;display:block;padding-top:12px;padding-bottom:12px' target='_blank'><strong>"+
                titleActionLink+"</strong></a></td></tr></tbody></table><p>"+contentPostActionLinkString+"</p><br><strong>Mmarazzu Team @ YanchWare</strong><br></td></tr></tbody></table>" + PostHtmlEmailContent;
    }

}
